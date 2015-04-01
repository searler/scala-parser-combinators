package scala.util.parsing.combinator

import scala.util.parsing.input.CharArrayReader

import org.junit.Test
import org.junit.Assert.assertEquals

class JavaTokenParsersTest {

  @Test
  def parseDecimalNumber: Unit = {
    object TestJavaTokenParsers extends JavaTokenParsers
    import TestJavaTokenParsers._
    assertEquals("1.1", decimalNumber(new CharArrayReader("1.1".toCharArray)).get)
    assertEquals("1.", decimalNumber(new CharArrayReader("1.".toCharArray)).get)
    assertEquals(".1", decimalNumber(new CharArrayReader(".1".toCharArray)).get)
    // should fail to parse and we should get Failure as ParseResult
    val failure = decimalNumber(new CharArrayReader("!1".toCharArray)).asInstanceOf[Failure]
    assertEquals("""(\d+(\.\d*)?|\d*\.\d+)""", failure.msg)
  }

  @Test
  def parseJavaIdent: Unit = {
    object javaTokenParser extends JavaTokenParsers
    import javaTokenParser._
    def parseSuccess(s: String): Unit = {
      val parseResult = parseAll(ident, s)
      parseResult match {
        case Success(r, _) => assertEquals(s, r)
        case _ => sys.error(parseResult.toString)
      }
    }
    def parseFailure(s: String, errorColPos: Int): Unit = {
      val parseResult = parseAll(ident, s)
      parseResult match {
        case Failure(_, next) =>
          val pos = next.pos
          assertEquals(1, pos.line)
          assertEquals(errorColPos, pos.column)
        case _ => sys.error(parseResult.toString)
      }
    }
    parseSuccess("simple")
    parseSuccess("with123")
    parseSuccess("with$")
    parseSuccess("with\u00f8\u00df\u00f6\u00e8\u00e6")
    parseSuccess("with_")
    parseSuccess("_with")

    parseFailure("3start", 1)
    parseFailure("-start", 1)
    parseFailure("with-s", 5)
    // we♥scala
    parseFailure("we\u2665scala", 3)
    parseFailure("with space", 6)
  }

}
