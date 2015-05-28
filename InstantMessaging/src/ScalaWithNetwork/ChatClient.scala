package ScalaWithNetwork


import java.net.Socket
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintStream
import scala.io.StdIn._
import scala.io.StdIn._
import scala.swing._
import scala.swing.TextField
import scala.swing.event._

object ChatClient {
  def main(args: Array[String]): Unit = {
    
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
      val button1 = new Button {
        text = "New Room"
      }
      val button2 = new Button {
        text = "Join"
      }
      val button3 = new Button {
        text = "Leave"
      }
      val button4 = new Button {
        text = "Exit"
      }
      val button5 = new Button {
        text = "Users"
      }
      val button6 = new Button {
        text = "Rooms"
      }
      val gridP = new GridPanel(3,3)
      {
        contents += button1
        contents += button2
        contents += button3
        contents += button4
        contents += button5
        contents += button6    
      }
      contents = new BoxPanel(Orientation.Vertical)
      {
        contents += new BorderPanel{  
          layout += new ScrollPane(textArea) -> BorderPanel.Position.Center
          layout += textField -> BorderPanel.Position.South
        }
        contents += gridP

      }
      var stringdoe = 1
      listenTo(button1)
      listenTo(button2)
      listenTo(button3)
      listenTo(button4)
      listenTo(button5) 
      listenTo (button6)
      reactions += { 
        case ButtonClicked(`button1`) =>
         //val popupMenu = new PopupMenu{
          //val textField2:TextField = new TextField("Input")
           // contents += textField2
            //listenTo(textField2)
            //reactions += {
            //case e:EditDone => 
              //os.println("\\new" + textField2.text)
            //}
         //}
          //popupMenu.show(button1, button1.bounds.height *2, button1.bounds.height *2)
          //listenTo(textField2)
          listenTo(textField.keys)
          reactions += {
            case KeyPressed(_, Key.Alt, _, _) => os.println("\\new " + textField.text)
          }
          //stringdoe += 1
        case ButtonClicked(`button2`) => 
          listenTo(textField.keys)
          reactions += {
            case KeyPressed(_, Key.Alt, _, _) => os.println("\\join " + textField.text)
          }
          
          //stringdoe += 1
        case ButtonClicked(`button3`) => os.println("\\leave")
        case ButtonClicked(`button4`) => os.println("\\exit")
        case ButtonClicked(`button5`) => os.println("\\users")
        case ButtonClicked(`button6`) => 
          os.println("\\list")
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
    
    //send message to server
    frame.open
  }

}