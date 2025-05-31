import scala.io.StdIn.readLine

def eleven(n:Int):Int = {
  math.pow(2, n).toInt
}

@main
def main(): Unit =


  println("Введите ваше имя:")
  val n = readLine()

