package ScalaWithNetwork

import java.net._
import java.io._
import collection.mutable

/**
 * class listens for connecting clients
 * actor is marked as deprecated, akka actors are now preferred
 */
object SimpleNetwork {
  //scala case class, compiler provides implementation of equals and hash value
  case class User(sock: Socket, is: BufferedReader, ps: PrintStream, name: String)

  def main(args: Array[String]): Unit = {
    //array of connected users
    val users = new mutable.ArrayBuffer[User] with mutable.SynchronizedBuffer[User]
    val ss = new ServerSocket(4444)
    
    //create new thread for incomming connections
    actors.Actor.actor{
      while(true){
        val sock = ss.accept()
        val is = new BufferedReader(new InputStreamReader(sock.getInputStream))
        val ps = new PrintStream(sock.getOutputStream)
        
        //readline blocks thread, create new thread to allow multiple people to login
        actors.Actor.actor{
          ps.println("What is your name? ")
          val name = is.readLine()
          users += User(sock,is,ps,name)
          println("registered user: " + name)
          println(sock.toString())
        }
      }
    }
    
    //wait for input 
    while(true){
      for(user <- users){
        if(user.is.ready()){
          val input = user.is.readLine()
          for(user2 <- users){
            user2.ps.println(user.name + ": " + input)
          }
        }
      }
    }
  }
  
}