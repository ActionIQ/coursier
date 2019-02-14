package coursier

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}
import java.util.Locale

import coursier.cache.{Cache, MockCache}
import coursier.util.Task

import scala.concurrent.{ExecutionContext, Future}

abstract class PlatformTestHelpers {

  private val mockDataLocation = {
    val dir = Paths.get("modules/tests/metadata")
    assert(Files.isDirectory(dir))
    dir
  }

  val writeMockData = sys.env
    .get("FETCH_MOCK_DATA")
    .exists(s => s == "1" || s.toLowerCase(Locale.ROOT) == "true")

  val cache: Cache[Task] =
    MockCache.create[Task](mockDataLocation, writeMissing = writeMockData)

  def textResource(path: String)(implicit ec: ExecutionContext): Future[String] =
    Future {
      val p = Paths.get(path)
      val b = Files.readAllBytes(p)
      new String(b, StandardCharsets.UTF_8)
    }

  def maybeWriteTextResource(path: String, content: String): Unit = {
    val p = Paths.get(path)
    Files.createDirectories(p.getParent)
    Files.write(p, content.getBytes(StandardCharsets.UTF_8))
  }

}
