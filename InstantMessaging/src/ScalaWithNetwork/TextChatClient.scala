package ScalaWithNetwork

import java.net.Socket
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintStream
import scala.io.StdIn._
import scala.swing._
import scala.swing.TextField
import scala.swing.event._


object TextChatClient {
  def main(args: Array[String]): Unit = {
    if(args.isEmpty)
    {
      println("Usage: ")
    }
    //we can modifiy these and ask the user 
    val host = "localhost"
    val port = 4444
    val sock = new Socket(host,port)
    
    val is = new BufferedReader(new InputStreamReader(sock.getInputStream()))
    val os = new PrintStream(sock.getOutputStream())
    var flag = true
    val textArea = new TextArea
    {
       editable = false
    }
    val textField = new TextField
    {
      listenTo(this)
      reactions += {
        case e:EditDone =>
          if(text.nonEmpty)
          {
            os.println(text)
            text = ""
          }
      }
    }
    val frame = new MainFrame{
      title = "408 Class: Team SWAG"
      contents = new BorderPanel{
        layout += new ScrollPane(textArea) -> BorderPanel.Position.Center
        layout += textField -> BorderPanel.Position.South
      }
      size = new Dimension(400,600)
      centerOnScreen
    }
    //new thread for writing server messages
    actors.Actor.actor {
      while(flag){
        if(is.ready()){
          val output = is.readLine()
          textArea.append(output + "\n")
        }
        Thread.sleep(100)
      }
    }
    frame.open

  }
}