package ScalaWithNetwork

import collection.mutable
import java.io._
import java.net._

object Lobby{
  
  private val users  = new mutable.ArrayBuffer[User] with mutable.SynchronizedBuffer[User]
  private val rooms = new mutable.ArrayBuffer[Room] with mutable.SynchronizedBuffer[Room]
  private val mainLobby = new Room("MainLobby", new User(null,null,null,"MainLobby"))
  
  def main(args: Array[String]): Unit = {
    
    //listening to server socket
    val ss = new ServerSocket(4444)
    
    //accept blocks thread, spawn new thread for this
    actors.Actor.actor{
      while(true){
        val sock = ss.accept()
        val is = new BufferedReader(new InputStreamReader(sock.getInputStream))
        val ps = new PrintStream(sock.getOutputStream)
        
        //readline blocks thread, spawn new thread for client
        actors.Actor.actor{
          ps.println("what is your name: ")
          ps.flush()
          val name = is.readLine()
          val newUser = new User(sock,is,ps,name)
          newUser.setRoom("MainLobby")
          users += newUser
          println("registered user " + newUser.name)
          mainLobby.joinRoom(newUser)
        }
      }
    }
    
    //read user input
    while(true){
      for(user <- users){
        if(user.is.ready()){
          val input = user.is.readLine()
          var temp = (input.split(" "))
          var opt = temp(0)
          var opt2 = ""
          if(temp.length > 1)
            opt2 = temp(1)
          opt match{
            case "\\new" => createRoom(opt2, user)
            case "\\help" => printCommands(user.ps)
            case "\\join" => joinRoom(user, opt2)
            case "\\leave"=> leaveRoom(user)
            case "\\exit" => exit(user)
            case "\\users" => printSessions(user)
            case "\\list" => printRooms(user)
            case "\\room" => user.ps.println("currently in " + user.getRoom())
            case _ => mainLobbyMessage(input, user)
          }
          
          
        }
      }
    }
  }
  
  def printRooms(user: User){
    user.ps.println("Rooms available")
    user.ps.println(mainLobby.roomName)
    for(room <- rooms){
      user.ps.println(room.roomName)
    }
  }
  
  def createRoom(roomName: String, user: User){
    mainLobby.leaveRoom(user)
    val newRoom = new Room(roomName,user)
    newRoom.joinRoom(user)
    rooms += newRoom
    user.setRoom(roomName)
  }
  
  def mainLobbyMessage(input: String, user: User){
    //get the user's room
    if(user.getRoom() == "MainLobby"){
       mainLobby.roomMessage(input, user.name)
    } else {
       for(room <- rooms){
          if(room.roomName == user.getRoom()){
             room.roomMessage(input, user.name)
          }
       }
    }
  }
  
  def printSessions(usr: User){
    if(usr.getRoom() == "MainLobby"){
      mainLobby.printUsers(usr)
    }
    for(room <- rooms){
      if(room.roomName == usr.getRoom()){
        room.printUsers(usr)
      }
    }
  }
  
  def joinRoom(usr: User, opt2: String){
    if(usr.getRoom() == opt2){
      usr.ps.println("Already in this room")
      return
    }
    for(tempRoom <- rooms){
      if(tempRoom.roomName == opt2){
         if(usr.getRoom() != "MainLobby"){
           leaveRoom(usr)
         }
         println("Joining room " + tempRoom.roomName)
         tempRoom.joinRoom(usr)
         usr.setRoom(tempRoom.roomName)
         return
      } 
    }
  }
  
  def leaveRoom(usr: User){
    if(usr.getRoom() == "MainLobby"){
      usr.ps.println("cannot leave main lobby, type exit to leave")
      return
    }
    for(tempRoom <- rooms){
      if(tempRoom.roomName == usr.getRoom()){
        usr.ps.println("leaving room " + tempRoom.roomName)
        tempRoom.leaveRoom(usr)
        mainLobby.joinRoom(usr)
        usr.setRoom("MainLobby")
        return
      }
    }
  }
  
  
  def exit(tempUser: User){
    tempUser.ps.println("Disconnected from server")
    users -= tempUser
    if(tempUser.getRoom() != "MainLobby"){
      leaveRoom(tempUser)
    }
    mainLobby.leaveRoom(tempUser)
    tempUser.is.close()
    tempUser.ps.close()
    println(tempUser.name + " has disconnected")
  }
  
  def printCommands(tempPS: PrintStream){
    tempPS.println("Printing available commands: "+
                  "\n\\help \n\\join \n\\leave \n\\exit"+
                  " \n\\users\n\\new \"roomName\" \n\\list"+
                  "\n\\room")
                  
  }
 
  
}