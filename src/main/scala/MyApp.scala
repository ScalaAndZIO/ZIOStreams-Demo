import zio.{App, UIO, ZIO}
import zio.console._
import zio.stream._

object MyApp extends App {

  val stream: Stream[Nothing, Int] = Stream(1, 2, 3)
  val stringStream: Stream[Nothing, Int] = stream.map( i => i*3)
  val result: ZIO[Console, Nothing, Unit] = stringStream.foreach(i => putStrLn(i.toString))

  def streamReduce(total: Int, element: Int): Int = total + element
  val resultFromSink: UIO[Int] = Stream(1,2,3).run(Sink.foldLeft(0)(streamReduce))
  val merged: Stream[Nothing, Int] = Stream(1,2,3).merge(Stream(2,3,4))
  val mergedResult: ZIO[Console, Nothing, Unit] = merged.foreach(i => putStrLn(i.toString))

  val zippedStream: Stream[Nothing, (Int, Int)] = Stream(1,2,3).zip(Stream(2,3,4))
  val resultZipped:ZIO[Console, Nothing, Unit] = zippedStream.foreach(i => putStrLn(i.toString))

  val resultZipped2: ZIO[Console,Nothing, Unit] = zippedStream.foreach(i => putStrLn((i._1 +"  " +i._2).toString()))

  def run(args: List[String]) =
    myAppLogic.fold(_ => 1, _ => 0)

  val myAppLogic =
    for {
      _ <- result
      rs <- resultFromSink
      _ <- putStrLn("Result from sink \n"+rs.toString)
      _ <- putStrLn("merged result")
      _ <- mergedResult
      _ <-putStrLn("zipped result ")
      _ <- resultZipped
      _ <-putStrLn("zipped result2 ")
      _ <- resultZipped2
    } yield ()
}