package com.example.plyground.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PriceTag(price: Double, modifier: Modifier = Modifier) {
    Text(
        text  = "$%.2f".format(price),
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
    )
}

@Composable
fun CategoryChip(category: String) {
    SuggestionChip(
        onClick = {},
        label  = { Text(category, style = MaterialTheme.typography.labelSmall) }
    )
}

@Composable
fun StarRating(rating: Float, modifier: Modifier = Modifier) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        repeat(5) { i ->
            Text(if (i < rating.toInt()) "★" else "☆",
                 color = MaterialTheme.colorScheme.tertiary)
        }
        Spacer(Modifier.width(4.dp))
        Text("$rating", style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun ProductMeta(
    price: Double,
    category: String,
    rating: Float
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            PriceTag(price)
            CategoryChip(category)
        }
        StarRating(rating)
    }
}
