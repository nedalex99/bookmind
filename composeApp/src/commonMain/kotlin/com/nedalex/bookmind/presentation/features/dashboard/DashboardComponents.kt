package com.nedalex.bookmind.presentation.features.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nedalex.bookmind.presentation.theme.*
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

// ============================================
// HEADER
// ============================================

@Composable
fun DashboardHeader(
    greeting: String,
    userName: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Stone50)
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        Text(
            text = greeting,
            fontSize = 13.sp,
            fontWeight = FontWeight.Normal,
            letterSpacing = 1.5.sp,
            color = Stone500,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = userName,
            fontSize = 36.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = FontFamily.Serif,
            color = Stone900
        )
    }
}

// ============================================
// CURRENTLY READING SECTION
// ============================================

@Composable
fun CurrentlyReadingSection(
    books: List<ReadingProgress>,
    onViewAll: () -> Unit,
    onBookClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Stone50)
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        SectionHeader(
            title = "CURRENTLY READING",
            actionText = "View All",
            onAction = onViewAll
        )

        Spacer(modifier = Modifier.height(24.dp))

        books.forEach { progress ->
            ReadingProgressCard(
                progress = progress,
                onClick = { onBookClick(progress.book.id) }
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun ReadingProgressCard(
    progress: ReadingProgress,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        // Book Cover
        Box(
            modifier = Modifier
                .width(80.dp)
                .height(120.dp)
                .background(Stone900)
                .border(1.dp, Stone200)
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Book Info
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = progress.book.title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = FontFamily.Serif,
                color = Stone900,
                lineHeight = 26.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = progress.book.author,
                fontSize = 15.sp,
                color = Stone500
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "${progress.progressPercentage}% complete",
                fontSize = 14.sp,
                color = Stone500
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(Stone200)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progress.progressPercentage / 100f)
                        .background(Stone900)
                )
            }
        }
    }
}

// ============================================
// RECOMMENDATIONS SECTION
// ============================================

@Composable
fun RecommendationsSection(
    recommendations: List<BookRecommendation>,
    onSeeAll: () -> Unit,
    onBookClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Stone50)
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        SectionHeader(
            title = "RECOMMENDED FOR YOU",
            actionText = "See All",
            onAction = onSeeAll
        )

        Spacer(modifier = Modifier.height(24.dp))

        recommendations.take(3).forEach { recommendation ->
            RecommendationCard(
                recommendation = recommendation,
                onClick = { onBookClick(recommendation.book.id) }
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun RecommendationCard(
    recommendation: BookRecommendation,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Text(
            text = recommendation.book.title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = FontFamily.Serif,
            color = Stone900,
            lineHeight = 23.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = recommendation.book.author,
            fontSize = 15.sp,
            color = Stone500
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Rating
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "★",
                    color = Stone900,
                    fontSize = 14.sp
                )
                Text(
//                    text = String.format("%.1f", recommendation.book.rating),
                    text = recommendation.book.rating.toString(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Stone900
                )
            }

            // Match Percentage
            Text(
                text = "${recommendation.matchPercentage}%",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Emerald600
            )
        }
    }
}

// ============================================
// STATS SECTION
// ============================================

@Composable
fun StatsSection(
    totalBooks: Int,
    totalPages: Int,
    totalHours: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Stone50)
            .padding(vertical = 24.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatItem(
            value = totalBooks.toString(),
            label = "BOOKS"
        )

        VerticalDivider()

        StatItem(
            value = formatNumber(totalPages),
            label = "PAGES"
        )

        VerticalDivider()

        StatItem(
            value = totalHours.toString(),
            label = "HOURS"
        )
    }
}

@Composable
private fun StatItem(
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Text(
            text = value,
            fontSize = 36.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = FontFamily.Serif,
            color = Stone900
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = Stone500,
            letterSpacing = 1.sp
        )
    }
}

@Composable
private fun VerticalDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(40.dp)
            .background(Stone200)
    )
}

private fun formatNumber(number: Int): String {
    return when {
//        number >= 1000 -> String.format("%.1fk", number / 1000.0)
        number >= 1000 -> (number / 1000.0).toString()
        else -> number.toString()
    }
}

// ============================================
// FRIEND ACTIVITY SECTION
// ============================================

@Composable
fun FriendActivitySection(
    activities: List<FriendActivity>,
    onActivityClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Stone50)
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        SectionHeader(
            title = "FRIEND ACTIVITY",
            actionText = null,
            onAction = {}
        )

        Spacer(modifier = Modifier.height(24.dp))

        activities.forEach { activity ->
            FriendActivityItem(
                activity = activity,
                onClick = { onActivityClick(activity.id) }
            )
        }
    }
}

@Composable
private fun FriendActivityItem(
    activity: FriendActivity,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Stone900)
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Activity Text
        Column(modifier = Modifier.weight(1f)) {
            val activityText = buildActivityText(activity)
            Text(
                text = activityText,
                fontSize = 15.sp,
                color = Stone900,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = formatTimestamp(activity.timestamp),
                fontSize = 13.sp,
                color = Stone500
            )
        }

        // Arrow
        Text(
            text = "›",
            fontSize = 20.sp,
            color = Stone400
        )
    }
}

private fun buildActivityText(activity: FriendActivity): String {
    return when (activity.activityType) {
        ActivityType.FINISHED -> "${activity.userName} finished ${activity.bookTitle}"
        ActivityType.STARTED -> "${activity.userName} started ${activity.bookTitle}"
        ActivityType.RATED -> "${activity.userName} rated ${activity.rating}★ ${activity.bookTitle}"
    }
}

@OptIn(ExperimentalTime::class)
private fun formatTimestamp(timestamp: Long): String {
    val now = Clock.System.now().toEpochMilliseconds()
    val diff = now - timestamp
    val hours = diff / (1000 * 60 * 60)
    val days = diff / (1000 * 60 * 60 * 24)

    return when {
        hours < 1 -> "Just now"
        hours < 24 -> "${hours}h ago"
        days == 1L -> "1d ago"
        else -> "${days}d ago"
    }
}

// ============================================
// EMPTY STATES FOR INDIVIDUAL SECTIONS
// ============================================

@Composable
fun EmptyCurrentlyReadingSection(
    onAction: (DashboardAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Stone50)
            .padding(horizontal = 24.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "📖",
            fontSize = 64.sp,
            color = Stone300.copy(alpha = 0.3f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "No Books Yet",
            fontSize = 20.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = FontFamily.Serif,
            color = Stone900
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Start reading by adding a book to your library",
            fontSize = 15.sp,
            color = Stone500,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { /* Navigate to add book */ },
            colors = ButtonDefaults.buttonColors(
                containerColor = Stone900,
                contentColor = Stone50
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            Text(
                text = "Add Your First Book",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
fun EmptyRecommendationsSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Stone50)
            .padding(horizontal = 24.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "✨",
            fontSize = 64.sp,
            color = Stone300.copy(alpha = 0.3f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Generating Recommendations",
            fontSize = 20.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = FontFamily.Serif,
            color = Stone900
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Add books to your library to get personalized recommendations",
            fontSize = 15.sp,
            color = Stone500,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
    }
}

@Composable
fun EmptyFriendActivitySection(
    onAction: (DashboardAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Stone50)
            .padding(horizontal = 24.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "👥",
            fontSize = 64.sp,
            color = Stone300.copy(alpha = 0.3f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "No Friends Yet",
            fontSize = 20.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = FontFamily.Serif,
            color = Stone900
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Connect with other readers to see what they're reading",
            fontSize = 15.sp,
            color = Stone500,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { /* Navigate to find friends */ },
            colors = ButtonDefaults.buttonColors(
                containerColor = Stone900,
                contentColor = Stone50
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            Text(
                text = "Find Friends",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }
    }
}

// ============================================
// SECTION HEADER
// ============================================

@Composable
private fun SectionHeader(
    title: String,
    actionText: String?,
    onAction: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 1.5.sp,
            color = Stone600
        )

        if (actionText != null) {
            Text(
                text = actionText,
                fontSize = 14.sp,
                color = Stone600,
                modifier = Modifier.clickable(onClick = onAction)
            )
        }
    }
}

// ============================================
// BOTTOM NAVIGATION
// ============================================

@Composable
fun DashboardBottomNav(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Stone50,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Column {
            Divider(color = Stone200, thickness = 1.dp)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp)
                    .padding(bottom = 20.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavItem(
                    label = "Home",
                    icon = "⌂",
                    isActive = currentRoute == "home",
                    onClick = { onNavigate("home") }
                )
                NavItem(
                    label = "Discover",
                    icon = "✦",
                    isActive = currentRoute == "discover",
                    onClick = { onNavigate("discover") }
                )
                NavItem(
                    label = "Library",
                    icon = "☰",
                    isActive = currentRoute == "library",
                    onClick = { onNavigate("library") }
                )
                NavItem(
                    label = "Profile",
                    icon = "👤",
                    isActive = currentRoute == "profile",
                    onClick = { onNavigate("profile") }
                )
            }
        }
    }
}

@Composable
private fun NavItem(
    label: String,
    icon: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = icon,
            fontSize = 22.sp,
            color = if (isActive) Stone900 else Stone400
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = if (isActive) Stone900 else Stone400
        )
    }
}

// ============================================
// EMPTY DASHBOARD CONTENT
// ============================================

@Composable
fun EmptyDashboardContent(
    modifier: Modifier = Modifier,
    userName: String,
    greeting: String,
    onAction: (DashboardAction) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Stone50)
    ) {
        // Header
        DashboardHeader(
            greeting = greeting,
            userName = userName
        )

        Divider(color = Stone200, thickness = 1.dp)

        // Quick Actions
        QuickActionsSection(onAction = onAction)

        Divider(color = Stone200, thickness = 1.dp)

        // Empty Stats
        EmptyStatsSection()

        Divider(color = Stone200, thickness = 1.dp)

        // Empty Currently Reading
        EmptySection(
            icon = "📖",
            title = "No Books Yet",
            description = "Start reading by adding a book to your library",
            buttonText = "Add Your First Book",
            onButtonClick = { /* Navigate to add book */ }
        )

        Divider(color = Stone200, thickness = 1.dp)

        // Empty Recommendations
        EmptySection(
            icon = "✨",
            title = "Generating Your Recommendations",
            description = "Our AI is analyzing your preferences. Start adding books to see personalized recommendations!",
            buttonText = null,
            onButtonClick = {}
        )

        Divider(color = Stone200, thickness = 1.dp)

        // Empty Friend Activity
        EmptySection(
            icon = "👥",
            title = "No Friends Yet",
            description = "Connect with other readers to see what they're reading",
            buttonText = "Find Friends",
            onButtonClick = { /* Navigate to find friends */ }
        )

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun QuickActionsSection(
    onAction: (DashboardAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Stone50)
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        Text(
            text = "QUICK ACTIONS",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 1.5.sp,
            color = Stone600
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionCard(
                modifier = Modifier.weight(1f),
                icon = "📚",
                title = "Add a Book",
                description = "Manually add books to your library",
                onClick = { /* Navigate to add book */ }
            )
            QuickActionCard(
                modifier = Modifier.weight(1f),
                icon = "📷",
                title = "Scan Book",
                description = "Use your camera to scan ISBN",
                onClick = { /* Navigate to scan */ }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionCard(
                modifier = Modifier.weight(1f),
                icon = "🔍",
                title = "Browse",
                description = "Explore popular books",
                onClick = { onAction(DashboardAction.NavigateToDiscover) }
            )
            QuickActionCard(
                modifier = Modifier.weight(1f),
                icon = "👥",
                title = "Find Friends",
                description = "Connect with readers",
                onClick = { /* Navigate to find friends */ }
            )
        }
    }
}

@Composable
private fun QuickActionCard(
    modifier: Modifier = Modifier,
    icon: String,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .background(Stone50)
            .border(1.dp, Stone200, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = icon,
            fontSize = 32.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = Stone900
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = description,
            fontSize = 12.sp,
            color = Stone500,
            lineHeight = 16.sp,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun EmptyStatsSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Stone50)
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        Text(
            text = "YOUR READING STATS",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 1.5.sp,
            color = Stone600
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            EmptyStatItem(value = "0", label = "BOOKS")
            VerticalDivider()
            EmptyStatItem(value = "0", label = "PAGES")
            VerticalDivider()
            EmptyStatItem(value = "0", label = "HOURS")
        }
    }
}

@Composable
private fun EmptyStatItem(
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Text(
            text = value,
            fontSize = 36.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = FontFamily.Serif,
            color = Stone300
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = Stone400,
            letterSpacing = 1.sp
        )
    }
}

@Composable
private fun EmptySection(
    icon: String,
    title: String,
    description: String,
    buttonText: String?,
    onButtonClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Stone50)
            .padding(horizontal = 24.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = icon,
            fontSize = 64.sp,
            color = Stone300.copy(alpha = 0.3f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = FontFamily.Serif,
            color = Stone900
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = description,
            fontSize = 15.sp,
            color = Stone500,
            lineHeight = 22.sp,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        if (buttonText != null) {
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onButtonClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Stone900,
                    contentColor = Stone50
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                Text(
                    text = buttonText,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
        }
    }
}