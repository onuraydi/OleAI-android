package com.onur.oleai_android.core.ui.utils

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.icons.lucide.Lucide

object IconMapper {
    fun getIconByName(name: String): ImageVector {
        val camelCaseName = name.split("-", "_").joinToString("") { part ->
            part.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }

        return try {
            // composables-icons-lucide uses extension properties like `val Lucide.ShoppingCart: ImageVector`
            // which compile to `ShoppingCartKt.getShoppingCart(Lucide)`
            val className = "com.composables.icons.lucide.${camelCaseName}Kt"
            val clazz = Class.forName(className)
            val method = clazz.getMethod("get$camelCaseName", Lucide::class.java)
            method.invoke(null, Lucide) as ImageVector
        } catch (e: Exception) {
            try {
                // If it is a direct property on the Lucide object (just in case)
                val method = Lucide::class.java.getMethod("get$camelCaseName")
                method.invoke(Lucide) as ImageVector
            } catch (ex: Exception) {
                // Fallback icon, safely fetched via reflection to avoid direct compilation errors if name changes
                try {
                    val clazz = Class.forName("com.composables.icons.lucide.UtensilsKt")
                    val method = clazz.getMethod("getUtensils", Lucide::class.java)
                    method.invoke(null, Lucide) as ImageVector
                } catch (fallbackEx: Exception) {
                    throw RuntimeException("Lucide icons not found: $name")
                }
            }
        }
    }
}
