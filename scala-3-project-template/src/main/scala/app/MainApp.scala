package app


import maath.{FirstTask, SecondTask, ThirdTask}
import scala.util.Random
import scala.util.Using
import java.util.Scanner

object MainApp {
  @main def run(): Unit = {



    println()
    println()
    println()




    // Задача 1 77a: Дано натуральное число n. Вычислить: 2^n 
    // https://ivtipm.github.io/Programming/Glava04/index04.htm#z77
    
    println("Task 1: 2^n ")
    println("Enter n:")
    val n1 = Using.resource(new Scanner(System.in))(_.nextInt())
    //написать как это работает
    val firstRrsuit = FirstTask.powerOfTwo(n1)
    println(f"First task result: $firstRrsuit")






    println()
    println()
    println()






    // Задача 2 178a: Даны натуральные числа n, a 1...an. Определить 
    //количество членов ak последовательности a1,...,an:
    //являющихся нечетными числами;
    //https://ivtipm.github.io/Programming/Glava07/index07.htm#z178

    //println("Task 2: количество нечетных чисел в последовательности")
    println("Task 2: the number of odd numbers in the sequence ")
    //println("Введите размер последовательности n:")
    println("Enter the sequence size n:")
    val n2 = Using.resource(new Scanner(System.in))(_.nextInt())
    val array = SecondTask.generateRandomSeq(n2)
    val secondResult = SecondTask.countOddNumbers(array)
    println(s"array: ${array.toArray.mkString(", ")}")
    println(s"Second task result: $secondResult")






    println()
    println()
    println()





    // Задача 3: Вычилить: сумма от k=1 до 10(k^3)сумма от l=1 до 15(k-l)^2
    //https://ivtipm.github.io/Programming/Glava10/index10.htm#z320
    
    
    println("Task 3: sum from k=1 to 10(k^3)sum from l=1 to 15(k-l)^2")
    println("Enter the first sum size size k:")
    val k = Using.resource(new Scanner(System.in))(_.nextInt())
    println("Enter the second sum size size l:")
    val l = Using.resource(new Scanner(System.in))(_.nextInt())
    val task3Result = ThirdTask.calculateDoubleSum(k,l)
    println(f"Third task result: $task3Result")





    println()
    println()
    println()
  }
}
