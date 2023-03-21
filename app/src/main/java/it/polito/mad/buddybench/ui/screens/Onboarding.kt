package it.polito.mad.buddybench.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import it.polito.mad.buddybench.ui.theme.DarkBlue
import it.polito.mad.buddybench.ui.theme.LightBlue
import it.polito.mad.buddybench.ui.theme.White

@Preview
@Composable
fun Onboarding() {

    Box(modifier = Modifier
        .fillMaxSize()
        .background(DarkBlue)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
        ) {
            Row(
                modifier = Modifier
                    .background(DarkBlue)
                    .fillMaxWidth()
                    .padding(top = 40.dp, start = 50.dp, end = 50.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    Icons.Filled.ArrowBack,
                    "Edit Pencil",
                    modifier = Modifier.size(40.dp, 40.dp),
                    tint = Color.White
                )
                Icon(
                    Icons.Filled.Edit,
                    "Edit Pencil",
                    modifier = Modifier.size(40.dp, 40.dp),
                    tint = Color.White
                )

            }
            Row(
                modifier = Modifier
                    .background(DarkBlue)
                    .fillMaxWidth()
                    .size(200.dp)
                    .padding(5.dp),
                horizontalArrangement = Arrangement.Center
            ) {
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
            Row(
                modifier = Modifier
                    .background(DarkBlue)
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(10.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = "Full Name",
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
                Text(text = "NickName",
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
                            .size(40.dp, 40.dp)
                        ,
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
                GameCard("Volleyball",
                    Icons.Outlined.Person,
                    10,
                    "Newbie"
                )
            }

        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameCard(name: String, sport: ImageVector, games: Int, level: String) {

    return Card (
        modifier = Modifier
            .size(200.dp, 180.dp)
            .padding(top = 30.dp, start = 20.dp)

    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top =  10.dp)
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
        Row() {
            MyBadge(level)
        }

    }
}


val map = mapOf(
    "Newbie" to LightBlue
)

@Composable
fun MyBadge(level: String) {

    val color : Color = map.get(level)!!
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


