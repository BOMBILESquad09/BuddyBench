package it.polito.mad.buddybench.ui.screens

import android.widget.ImageButton
import android.widget.ScrollView
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import it.polito.mad.buddybench.ui.theme.DarkBlue
import it.polito.mad.buddybench.ui.theme.LightBlue
import it.polito.mad.buddybench.ui.theme.*
import it.polito.mad.buddybench.ui.theme.White

class EditState() {
    var state by mutableStateOf(false)
        private set

    fun setEditMode(edit: Boolean) {
        this.state = edit
    }

}


@Preview
@Composable
fun Onboarding() {

    val editMode = EditState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBlue)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier
                    .background(DarkBlue)
                    .fillMaxWidth()
                    .padding(top = 40.dp, start = 50.dp, end = 50.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = {
                    editMode.setEditMode(false)
                    println("Returned To Edit Mode")
                }) {
                    Icon(
                        Icons.Filled.ArrowBack,
                        "Edit Pencil",
                        modifier = Modifier.size(40.dp, 40.dp),
                        tint = Color.White,
                    )
                }
                IconButton(onClick = {
                    editMode.setEditMode(true)
                    println("Passed To Edit Mode")
                }) {
                    AnimatedVisibility(visible = !editMode.state) {
                        Icon(
                            Icons.Filled.Edit,
                            "Edit Pencil",
                            modifier = Modifier.size(40.dp, 40.dp),
                            tint = Color.White
                        )
                    }

                }
            }
            Row(
                modifier = Modifier
                    .background(DarkBlue)
                    .fillMaxWidth()
                    .size(200.dp)
                    .padding(5.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Box {
                        Badge(
                            modifier = Modifier
                                .size(50.dp)
                                .zIndex(1f)
                        ) {
                            Icon(
                                Icons.Default.Phone,
                                "Edit Pencil",
                                modifier = Modifier.size(40.dp, 40.dp),
                                tint = Color.White
                            )
                        }
                    Image(
                        painter = painterResource(it.polito.mad.buddybench.R.drawable.ic_launcher_background),
                        contentDescription = "avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(200.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color.Gray, CircleShape)
                    )

                }
            }
            Row(
                modifier = Modifier
                    .background(DarkBlue)
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(10.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Full Name",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = White
                )
            }
            Row(
                modifier = Modifier
                    .background(DarkBlue)
                    .fillMaxWidth()
                    .height(60.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "NickName",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Light,
                    color = White
                )
            }
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Outlined.LocationOn,
                        "Pointer Location",
                        modifier = Modifier
                            .size(40.dp, 40.dp),
                        tint = Color.White,
                    )
                    Text(
                        text = "Turin",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = White
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "23 years old",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = White
                    )
                }
            }
            Row(
                modifier = Modifier
                    .background(DarkBlue)
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "86",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = White
                    )
                    Text(
                        text = "Played",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = White,
                        modifier = Modifier
                            .padding(top = 10.dp)
                    )
                }
                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "40",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = White
                    )
                    Text(
                        text = "Organized",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = White,
                        modifier = Modifier
                            .padding(top = 10.dp)
                    )
                }
                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "10%",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = White
                    )
                    Text(
                        text = "Reliability",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = White,
                        modifier = Modifier
                            .padding(top = 10.dp)
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 40.dp),
            ) {
                Text(
                    text = "Sports",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = White
                )
            }

            Row(
                modifier = Modifier.fillMaxHeight()
            ) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(start = 20.dp, bottom = 30.dp, end = 20.dp, top = 20.dp)
                ) {
                    items(simpleList, itemContent = {
                        GameCard(
                            "Volleyball",
                            Icons.Outlined.Person,
                            10,
                            level = it
                        )
                    })
                }
            }
        }

    }
}

val map = mapOf(
    "Newbie" to NewbieGreen,
    "Amateur" to AmateurGreen,
    "Skilled" to SkilledOrange,
    "Professional" to ProfessionalRed,
    "World Class" to WorldClassBlack

)

val simpleList =
    listOf("Professional", "Newbie", "Amateur", "Skilled", "Professional", "World Class")


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameCard(name: String, sport: ImageVector, games: Int, level: String) {

    return Card(
        modifier = Modifier
            .size(200.dp, 180.dp)
            .padding(top = 10.dp, start = 20.dp)

    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
        ) {
            Text(
                text = name,
                color = DarkBlue,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold
            )
            Icon(
                sport,
                "Pointer Location",
                modifier = Modifier
                    .size(40.dp, 40.dp),
                tint = DarkBlue,
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "$games games played",
                fontWeight = FontWeight.SemiBold
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            MyBadge(level)
        }

    }
}

@Composable
fun MyBadge(level: String) {

    val color: Color = map.get(level)!!
    Badge(
        modifier = Modifier
            .height(50.dp)
            .padding(top = 20.dp, start = 20.dp, end = 20.dp)
            .fillMaxWidth(),
        containerColor = color,
        contentColor = DarkBlue,
    ) {
        Text(
            text = level,
            color = White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}


