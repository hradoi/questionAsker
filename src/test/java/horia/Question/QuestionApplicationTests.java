package horia.Question;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.Assert.assertEquals;

@SpringBootTest
class QuestionApplicationTests {

	@Test
	public void test_me() {
		assertEquals("How old is David Cameron", 53, Question.ask("How old is David Cameron"));
		assertEquals("What is the birth name of Tony Blair ?", "Anthony Charles Lynton Blair", Question.ask("What is the birth name of Tony Blair ?"));
		assertEquals("How old is Tony Blair", 66, Question.ask("How old is Tony Blair?"));
	}

	@Test
	public void testNameParser() {
		assertEquals("Tony_Blair", Question.parseName("Tony Blair"));
		assertEquals("Tony_Blair", Question.parseName("tony blair"));
		assertEquals("Tony_Blair", Question.parseName("Tony Blair?"));
		assertEquals("Tony_Blair", Question.parseName("   Tony Blair   "));
		assertEquals("Tony_Blair", Question.parseName("   Tony Blair   ?   "));
		assertEquals("Tony_Blair", Question.parseName("tOnY BlAiR"));

		assertEquals("Tupac", Question.parseName("Tupac"));
		assertEquals("Joan_Of_Arc", Question.parseName("joan of arc"));

	}

	@Test
	public void testAgeParser() {
		assertEquals(Question.ageQuestion("Tony blair"), Integer.valueOf(66));
		assertEquals(Question.ageQuestion("Angela Merkel"), Integer.valueOf(65));
		assertEquals(Question.ageQuestion("Bill Gates"), Integer.valueOf(64));
		assertEquals(Question.ageQuestion("William Henry Gates III "), Integer.valueOf(-1));
		assertEquals(Question.ageQuestion("Horia Radoi"), Integer.valueOf(-1));
	}

	@Test
	public void testAgeQuery() {
		assertEquals(66, Question.ask("How old is Tony Blair?"));
		assertEquals(64, Question.ask("How old is bill gates"));
		assertEquals("No date of birth found for query \"How old is horia radoi\"", Question.ask("How old is horia radoi"));
	}

	@Test
	public void testNameQuery() {
		assertEquals("Anthony Charles Lynton Blair", Question.ask("What is the birth name of Tony Blair ?"));
		assertEquals("Charles John Huffam Dickens", Question.ask("What is the birth name of Charles Dickens?"));
		assertEquals("Farrokh Bulsara", Question.ask("What is the birth name of Freddie Mercury?"));

		// If they update: Felix Arvid Ulf Kjellberg
		assertEquals("No full name was found for Pewdiepie" , Question.ask("What is the birth name of Pewdiepie"));
		// maybe soon ^_^: Horia Stefan Radoi
		assertEquals("No full name was found for Horia_Radoi", Question.ask("What is the birth name of Horia Radoi ?"));
	}
}