import zio._
import zio.console.putStrLn
import zio.stream._


object InnerProduct extends App {

  val stream1: Stream[Nothing, Int] = Stream.fromIterable(0 to 1000)//Stream(1,2,3,4)
  val stream2: Stream[Nothing, Int] = Stream.fromIterable(1000 to 2000)//Stream(4,5,6,7)


  def inner_fold [A,B,C,D,E](stream1:Stream[Nothing,A])(stream2:Stream[Nothing,B])(tms: (A, B) => C)(pls: (D, C) => D)(zero: D)= for{
    acc <- Ref.make(zero)
    acc2 <- acc.get
    i = 0
    compStream = stream1.zip(stream2)
    str = compStream.map(i => tms(i._1,i._2))
    streamFold<- str.fold(acc2)(pls)
    _ <-acc.set(streamFold)
  } yield acc

  def inner_forEach [A,B,C,D,E](stream1:Stream[Nothing,A])(stream2:Stream[Nothing,B])(tms: (A, B) => C)(pls: (D, C) => D)(zero: D)= for{
    acc <- Ref.make(zero)
    compStream = stream1.zip(stream2)
    _ <-  compStream.foreach { case(a,b)=> acc.set(pls(unsafeRun(acc.get) ,tms(a,b)))  }
  } yield acc

  override def run(args: List[String]) =
    for {
      _<- putStrLn("Stream(0,1000) innerProduct Stream(1000,2000)")
     t1 <- IO.succeed(System.nanoTime)
    res <- inner_fold[Int, Int, Int, Int,Int](stream1)(stream2)(_ * _)(_ + _ )(0)
    res2 <-res.get
     t2 <- IO.succeed(System.nanoTime)
     time <- IO.succeed(t2-t1)
    _ <- putStrLn("Result in case Zip,Map & FlatMap = " + res2+" Time elapsed = " + time/1000000000.0 + " s")
//     --------------Time using foreach ---------------------------------------
     t3 <- IO.succeed(System.nanoTime)
     res3 <- inner_fold[Int, Int, Int, Int,Int](stream1)(stream2)(_ * _)(_ + _ )(0)
     res4 <-res3.get
     t4 <- IO.succeed(System.nanoTime)
     time <- IO.succeed(t4-t3)
      _ <- putStrLn("Result in case Foreach = " + res4 +" Time elapsed = " + time/1000000000.0 + " s")
    } yield (0)

}