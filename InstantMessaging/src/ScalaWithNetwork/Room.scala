package ScalaWithNetwork

import collection.mutable
import java.io._

class Room(val roomName: String, val admin: User) {
  
   private val sessions = new mutable.ArrayBuffer[User] with mutable.SynchronizedBuffer[User]
   sessions += admin
   
   def joinRoom(newUser: User){
     for(temp <- sessions){
       if(temp.getUserId() == newUser.getUserId()){
         newUser.ps.println("User already in this lobby")
       }
     }
     sessions += newUser
   }
   
   def leaveRoom(user: User): Boolean = {
     var index = -1
     for(temp <- sessions){
       if(temp.getUserId() == user.getUserId()){
         sessions -= user
         true
       }
     }
     false
   }
   
   def printUsers(usr: User){
     usr.ps.println("Users in room " + usr.getRoom())
     for(user <- sessions){
         if(user.name != "MainLobby")
           usr.ps.println(user.name)
     }
     return
   }
   
   def roomMessage(message: String, sender: String){
     for(temp <- sessions){
       if(temp.getUserId() != sender){
         temp.ps.println(sender + ": " + message)
       }
     }
   }
  
}