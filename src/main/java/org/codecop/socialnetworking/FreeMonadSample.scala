import scala.annotation.targetName
import scala.collection.mutable

object FreeMonad {
  trait Monad[M[_]] {
    def pure[A](a: A): M[A]
    def flatMap[A, B](ma: M[A])(f: A => M[B]): M[B]
  }

  object Monad {
    def apply[M[_]](using monad: Monad[M]): Monad[M] = monad
  }

  @targetName("Morphism")
  trait ~>[F[_], G[_]] {
    def apply[A](fa: F[A]): G[A]
  }

  trait Free[M[_], A] {
    import Free.*
    def flatMap[B](f: A => Free[M, B]): Free[M, B] = FlatMap(this, f)
    def map[B](f: A => B): Free[M, B] = flatMap(a => pure(f(a)))
    def foldMap[G[_]: Monad](natTrans: M ~> G): G[A] = this match {
      case Pure(a) => Monad[G].pure(a)
      case Suspend(ma) => natTrans.apply(ma)
      case FlatMap(fa, f) => // need a G[B]
        Monad[G].flatMap(fa.foldMap(natTrans))(a => f(a).foldMap(natTrans) )
    }
  }

  object Free {
    def pure[M[_], A](a: A): Free[M, A] = Pure(a)
    def liftM[M[_], A](ma: M[A]): Free[M, A] = Suspend(ma)

    case class Pure[M[_], A](a: A) extends Free[M, A]
    case class FlatMap[M[_],A,B](fa: Free[M, A], f: A => Free[M, B]) extends Free[M, B]
    case class Suspend[M[_], A](ma: M[A]) extends Free[M, A]
  }

  // sequence computations as data structures, THEN attach the monadic type at the end
  trait DBOps[A]
  case class Create[A](key: String, value: A) extends DBOps[Unit]
  case class Read[A](key: String) extends DBOps[A]
  case class Update[A](key: String, value: A) extends DBOps[A]
  case class Delete(key: String) extends DBOps[Unit]

  // definitions - fancier algebra
  type DBMonad[A] = Free[DBOps, A]

  // "smart" constructors
  def create[A](key: String, value: A): DBMonad[Unit] =
    Free.liftM[DBOps, Unit](Create(key, value))

  def get[A](key: String): DBMonad[A] =
    Free.liftM[DBOps, A](Read[A](key))

  def update[A](key: String, value: A): DBMonad[A] =
    Free.liftM[DBOps, A](Update[A](key, value))

  def delete(key: String): DBMonad[Unit] =
    Free.liftM(Delete(key))

  // business logic is FIXED
  def myLittleProgram: DBMonad[Unit] = for { // monadic
    _ <- create[String]("123-456", "Daniel")
    name <- get[String]("123-456")
    _ <- create[String]("567", name.toUpperCase())
    _ <- delete("123-456")
  } yield () // description of a computation

  // evaluate the program - interpreter/"compiler"
  // IO
  case class IO[A](unsafeRun: () => A)
  object IO {
    def create[A](a: => A): IO[A] = IO(() => a)
  }

  given ioMonad: Monad[IO] with {
    override def pure[A](a: A) = IO(() => a)
    override def flatMap[A, B](ma: IO[A])(f: A => IO[B]) =
      IO(() => f(ma.unsafeRun()).unsafeRun())
  }

  val myDB: mutable.Map[String, String] = mutable.Map()
  // TODO replace these with some real serialization
  def serialize[A](a: A): String = a.toString
  def deserialize[A](value: String): A = value.asInstanceOf[A]

  // nat trans DBOps -> IO
  // Natural transformation: Transforming one Functor into another and keeping the internal structure.
  // For each operation in DbOps there must be a interpretation in IO
  val dbOps2IO: DBOps ~> IO = new (DBOps ~> IO) {
    override def apply[A](fa: DBOps[A]): IO[A] = fa match {
      case Create(key, value) => IO.create { // actual code that uses the database
        println(s"insert into people(id, name) values ($key, $value)")
        myDB += (key -> serialize(value))
        ()
      }
      case Read(key) => IO.create {
        println(s"select * from people where id=$key limit 1")
        deserialize(myDB(key))
      }
      case Update(key, value) => IO.create {
        println(s"update people(name=$value) where id=$key")
        val oldValue = myDB(key)
        myDB += (key -> serialize(value))
        deserialize(oldValue)
      }
      case Delete(key) => IO.create {
        println(s"delete from people where id=$key")
        ()
      }
    }
  }

  val ioProgram: IO[Unit] = myLittleProgram.foldMap(dbOps2IO)

  def main(args: Array[String]): Unit = {
    ioProgram.unsafeRun() // PERFORMS THE ACTUAL WORK
  }
}
