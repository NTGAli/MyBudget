package com.ntg.core.model.res


data class CategoryRes(
    val income: List<Category>?,
    val expense: List<Category>?,
)

data class Category(
    val id: Int,
    val name: String,
    val type: Int,
)