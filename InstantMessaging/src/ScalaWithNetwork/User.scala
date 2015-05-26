package ScalaWithNetwork

import java.io._
import java.net._
import java.util.UUID

class User(val sock: Socket,val is: BufferedReader,val ps: PrintStream,val name: String) {

  private val userUUID = UUID.randomUUID()
  private var room = "";
  
  def getUserId(): String = {
    userUUID.toString()
  }
  
  def setRoom(newRoom: String){
    if(newRoom == null){
      println("attempted to asign empty room")
    } else {
      room = newRoom
    }
  }
  
  def getRoom(): String = {
    return room
  }
  
}