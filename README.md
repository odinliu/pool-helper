# Pool-Helper
Some useful pool helper. A pure Scala library.

# Using in your project
An example SBT dependency string for this library would look like this:
```scala
val pool-helper = "com.odinliu.util" %% "pool-helper" % "1.0.0"
```

# Usage
## resource pool
When some resource pool implemented`AutoCloseable`, and you have to close it manually.

### using
`using`only ensure close the resource, you should catche any exception yourself.
```scala
import com.odinliu.util.pool.resource.AutoReleaseHelper

object Hello extends App with AutoReleaseHelper {
  val mi: String = using(new BufferedReader(new FileReader(f))) { br =>
    // do something with br
    "returned string"
  }
  println(mi == "returned string")
}

```

### autoClose
`autoClose`returns an `Either[Unit, T]`, a more functionally way.
```scala
import com.odinliu.util.pool.resource.AutoReleaseHelper

object Hello extends App with AutoReleaseHelper {
  val jp = JedisPool() // jedis client pool
  val ret: Either[Unit, Option[String]] = autoClose(jp.getResource)({ jedis =>
    val v = jedis.get("Some-KEY")
    if (v == null) {
      None
    } else {
      Some(v)
    }
  }, t => warn("get get key failed", t))
}
```

`t => warn("get get key failed", t)` is an function you deal with exceptions(`Throwable`).

## concurrent pool
### max concurrency pool
`ConcurrentPool` limits max concurrency.
```scala
import com.odinliu.util.pool.concurrent.{ConcurrentPool, PoolHelper}

import scala.concurrent.{ExecutionContext, Future}

class SomeServiceImpl extends SomeService with PoolHelper {
  override val executor: ExecutionContext = scala.concurrent.ExecutionContext.global
  val maxConnection = Runtime.getRuntime().availableProcessors()
  val cp = new ConcurrentPool[SomeClient](maxConnection) {
    override protected def makeItem(): Future[Int] = {
      Future.successful(SomeClientFactory.newClient())
    }
  }

  def callSomeClient(params: Param): Future[Result] = {
    borrow(cp)(client => {
      client.doSomething(params)
    }, t => warn("call SomeClient failed", t))
      .flatMap {
        case Left(_) => Future(Result.FAILED)
        case Right(x) => x
      }
  }
}
```

### time-based limit pool
`TimedLimitsPool` can limit max qps or other time-based limits.
```scala
import com.odinliu.util.pool.concurrent.{TimedLimitsPool, PoolHelper}
import scala.concurrent.{ExecutionContext, Future}
import java.util.concurrent.TimeUnit

class SomeServiceImpl extends SomeService with PoolHelper {
  private val client: SomeClient = SomeClientFactory.newClient()

  override val executor: ExecutionContext = scala.concurrent.ExecutionContext.global

  val maxQps = 1000
  // use `Int` as token
  val limitsCp = new TimedLimitsPool[Int](maxQps, refresh = 1000L, tu = TimeUnit.MILLISECONDS) {
    var count: Int = -1
    override protected def makeItem(): Future[Int] = {
      count += 1
      Future.successful(count)
    }
  }

  def callSomeClient(params: Param): Future[Result] = {
    ration(limitsCp)(id => {
      client.doSomething(params)
    }, t => warn("call SomeClient failed", t))
      .flatMap {
        case Left(_) => Future(Result.FAILED)
        case Right(x) => x
      }
  }
}
```
