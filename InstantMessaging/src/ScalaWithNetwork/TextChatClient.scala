package ScalaWithNetwork

import java.net.Socket
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintStream
import scala.io.StdIn._



object TextChatClient {
  def main(args: Array[String]): Unit = {
    
    //we can modifiy these and ask the user 
    val host = "localhost"
    val port = 4444
    val sock = new Socket(host,port)
    
    val is = new BufferedReader(new InputStreamReader(sock.getInputStream()))
    val os = new PrintStream(sock.getOutputStream())
    var flag = true
    
    //new thread for writing server messages
    actors.Actor.actor {
      while(flag){
        if(is.ready()){
          val output = is.readLine()
          println(output)
        }
        Thread.sleep(100)
      }
    }
    
    //send message to server
    while(flag){
      val input = readLine
      if(input =="quit")
         flag = false
      else
        os.println(input)
    }
    sock.close()
    is.close()
    os.close()
  }
}