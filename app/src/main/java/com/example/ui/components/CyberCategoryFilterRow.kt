package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * ============================================================================
 * [Vulnera Lab v2.1 UI Component - Category Filter Row]
 * Provides filtering by threat vector type (Stalkerware, Rootkit, AMTSO, Spyware).
 * ============================================================================
 */
@Composable
fun CyberCategoryFilterRow(
  categories: List<String>,
  selectedCategory: String,
  onSelectCategory: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  Column(modifier = modifier.padding(horizontal = 20.dp)) {
    Text(
      text = "THREAT VECTOR CATALOG",
      style = MaterialTheme.typography.labelSmall,
      color = MaterialTheme.colorScheme.primary,
      letterSpacing = 1.8.sp,
      fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(8.dp))
    LazyRow(
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      items(categories) { cat ->
        val isSelected = selectedCategory == cat
        Surface(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onSelectCategory(cat) }
            .testTag("filter_chip_$cat"),
          color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh,
          border = BorderStroke(
            1.dp,
            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
          ),
          shape = RoundedCornerShape(12.dp)
        ) {
          Text(
            text = cat,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
          )
        }
      }
    }
  }
}
