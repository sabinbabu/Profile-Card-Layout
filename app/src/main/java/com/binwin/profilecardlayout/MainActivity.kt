package com.binwin.profilecardlayout

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.Start
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import com.binwin.profilecardlayout.ui.theme.ProfileCardLayoutTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProfileCardLayoutTheme {
                UserApplication()
            }
        }
    }
}

//Navigation
@Composable
fun UserApplication(){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "users_list"){
        composable("users_list") {
            UserListScreen(userData,navController) // have to pass navController to destination composable
        }
        composable(route = "user_detail/{userId}",
        arguments = listOf(navArgument("userId"){
            type = NavType.IntType
        })
        ) {navBackStackEntry ->   //getting argument from previous screen through navBackstack
            UserDetailScreen(navBackStackEntry.arguments!!.getInt("userId"),navController)
        }
    }
}

@Composable
fun UserListScreen(users : ArrayList<UserProfile> = userData, navHostController: NavHostController ) {
    Scaffold(topBar = {AppBar(icon = Icons.Default.Home, "Messaging Application" ){} }) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.LightGray
        ) {
            Column {
                Surface(modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth()) {
                }
                LazyColumn{
                    items(users){ user ->
                        ProfileCard(user){
                            navHostController.navigate("user_detail/${user.id}") //performing navigation and sending argument
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppBar(icon: ImageVector, title: String, onClickBack:()-> Unit){
   TopAppBar(
       navigationIcon = {Icon(
           imageVector = icon,
           contentDescription = "App Icon",
           Modifier.padding(horizontal = 12.dp)
               .clickable(onClick = onClickBack)
            )},
       title = { Text(text = title) }
   )
}

@Composable
fun ProfileCard(user: UserProfile, clickAction : ()-> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(Alignment.Top)
            .padding(start = 8.dp, top = 4.dp, end = 8.dp, bottom = 8.dp)
            .clickable(onClick = clickAction)
            ,
        elevation = 8.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically)
        {
                ProfilePicture(user,100.dp)
                ProfileContent(user,false)
        }
    }
}

@Composable
fun ProfilePicture(user: UserProfile, size : Dp) {
    Card(
        shape = CircleShape,
        border = BorderStroke(4.dp,if(user.status)Color.Green else Color.Red ),
        elevation = 4.dp,
        modifier = Modifier
            .size(size)
            .padding(8.dp)
    ) {
        AsyncImage(
            model = user.profilePicture,
            contentDescription = null
        )
    }
}

@Composable
fun ProfileContent(user: UserProfile, isProfile : Boolean) {
    Column {
        CompositionLocalProvider( LocalContentAlpha provides
                if (user.status) ContentAlpha.high  else ContentAlpha.medium){
        Text(
            text = user.name,
            style = MaterialTheme.typography.h5
        )
        }
        CompositionLocalProvider( LocalContentAlpha provides ContentAlpha.medium) {
            Text(
                text = if (user.status)"Active Now" else "Offline",
                style = MaterialTheme.typography.body2,
                modifier = if (isProfile )Modifier.align(CenterHorizontally) else Modifier.align(Start)
            )
        }
    }
}

@Composable
fun UserDetailScreen(userId : Int,navHostController: NavHostController?) {
    val user = userData.first { user -> userId == user.id }
    Scaffold(topBar = {
        AppBar(
            Icons.Default.ArrowBack,
            user.name){
            navHostController?.navigateUp()
        }
        }) {
        Surface(
            modifier = Modifier.fillMaxSize(),
        ) {
            Column(
                horizontalAlignment = CenterHorizontally
            ) {
                ProfilePicture(user,240.dp)
                ProfileContent(user,true)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UserDetailScreenPreview() {
    ProfileCardLayoutTheme {
        UserDetailScreen(0,null)
    }
}

@Preview(showBackground = true)
@Composable
fun UserListScreenPreview() {
    ProfileCardLayoutTheme {
        UserApplication()
    }
}