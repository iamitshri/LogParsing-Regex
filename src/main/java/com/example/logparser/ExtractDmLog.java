package com.example.logparser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ExtractDmLog {

	public static void test() {

		final String regex = "^(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d+).*?NotificationService.sendToDreamCatcher.*?submissionId:(.*)?,\\sstatus:\\s(-?\\d+)$";
		final String string = "2018-06-05 21:50:42.504 DEBUG 4659 --- [nio-8274-exec-9] e.w.d.service.NotificationService        : NotificationService.sendToDreamCatcher(): Finished processing for studentId: QA0000001, assessment: MLT2, taskId: 21aa2b4b-3e07-4ba9-b13f-f3e28da6d2ae, submissionId: 405b7303-2696-49c5-9934-583a9e972528, status: -2\n";

		final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE | Pattern.DOTALL);
		final Matcher matcher = pattern.matcher(string);

		while (matcher.find()) {
			System.out.println("Full match: " + matcher.group(0));
			for (int i = 1; i <= matcher.groupCount(); i++) {
				System.out.println("Group " + i + ": " + matcher.group(i));
			}
		}
	}
	
	static String readFile(String fileName) throws IOException {
		Path path = Paths.get("/Users/amit.shrigondekar/logs/6-5", fileName);
		String str = new String(Files.readAllBytes(path));
		return str;
	}
	
	public static void extractCombined() throws IOException {
		 
		String file = readFile("sample-test.log");

	 

		final String regex1 = "^(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d+).*?NotificationService.sendAssessment\\(\\) sending to Dream Catcher:((.|\n)*?)\\d{4}-\\d{2}-\\d{2}";
		final String regex2 = "^(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d+).*?NotificationService.sendToDreamCatcher\\(\\): Finished processing.*?submissionId:(.*)?,\\sstatus:\\s(-?\\d+)$";
		final Pattern pattern = Pattern.compile(regex1+"|"+regex2, Pattern.MULTILINE );
		Matcher matcher = pattern.matcher(file);
		while (matcher.find()) {
			 

			System.out.println("Full match: " + matcher.group(0));
			for (int i = 1; i <= matcher.groupCount(); i++) {
				System.out.println("Group " + i + ": " + matcher.group(i));
			}
		}

	}
	
	public static void extractAssessmentModelsSent() throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		String file = readFile("sample-test.1.log");

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

		final String regex = "^(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d+).*?NotificationService.sendAssessment\\(\\) sending to Dream Catcher:((.|\n)*?)\\d{4}-\\d{2}-\\d{2}";
		final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE );
		Matcher matcher = pattern.matcher(file);
		while (matcher.find()) {
			/*
			 * String dateOfLogEntry = matcher.group(1); LocalDateTime dateTimeofLog =
			 * LocalDateTime.parse(dateOfLogEntry, formatter);
			 * System.out.println(" dateOfLogEntry: " + dateTimeofLog); AssessmentModel
			 * asmtModel = mapper.readValue(matcher.group(2), AssessmentModel.class);
			 * System.out.println(""+mapper.writeValueAsString(asmtModel));
			 */

			System.out.println("Full match: " + matcher.group(0));
			for (int i = 1; i <= matcher.groupCount(); i++) {
				System.out.println("Group " + i + ": " + matcher.group(i));
			}
		}

	}
	
	public static void extractFinishedProcessingMsgs() throws IOException {

		final String regex = "^(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d+).*?NotificationService.sendToDreamCatcher\\(\\): Finished processing.*?submissionId:(.*)?,\\sstatus:\\s(-?\\d+)$";
		String string = "2018-06-05 21:50:42.504 DEBUG 4659 --- [nio-8274-exec-9] e.w.d.service.NotificationService        : NotificationService.sendToDreamCatcher(): Finished processing for studentId: QA0000001, assessment: MLT2, taskId: 21aa2b4b-3e07-4ba9-b13f-f3e28da6d2ae, submissionId: 405b7303-2696-49c5-9934-583a9e972528, status: -2\n";
		String fileString = readFile("sample-test.log");
		final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE );
		final Matcher matcher = pattern.matcher(fileString);

		while (matcher.find()) {
			System.out.println("Full match: " + matcher.group(0));
			for (int i = 1; i <= matcher.groupCount(); i++) {
				System.out.println("Group " + i + ": " + matcher.group(i));
			}
		}
	}
	
	void testMatch() {
		final String regex = "NotificationService.sendToAssessmentToRabbit.*?sending to Rabbit:(.*?)\\d{4}-\\d{2}-\\d{2}";
		final String string = "2018-06-04 16:55:37.593 DEBUG 3246 --- [nio-8274-exec-9] e.w.d.service.NotificationService        : NotificationService.sendToAssessmentToRabbit() sending to Rabbit: {\n"
				+ "  \"studentId\" : \"QA0000013\",\n" + "  \"assessmentCode\" : \"ABC2\",\n"
				+ "  \"assessmentId\" : \"3817c2b9-e9d6-4013-92fc-560d8b54ee95\",\n" + "  \"tasks\" : [ {\n"
				+ "    \"submissionId\" : \"a39b3c57-c3c7-4714-9c8d-c2fb6f7ac42f\",\n" + "    \"evaluatorId\" : null,\n"
				+ "    \"taskId\" : \"b6fa3981-88d2-4610-967e-5284ed4a3543\",\n"
				+ "    \"taskName\" : \"Rabbit test task 1\",\n" + "    \"status\" : 16,\n"
				+ "    \"dateUpdated\" : 1528131303771,\n" + "    \"number\" : 1\n" + "  }, {\n"
				+ "    \"submissionId\" : \"5175c049-57b9-4db6-b10d-c7d3696bbc11\",\n" + "    \"evaluatorId\" : null,\n"
				+ "    \"taskId\" : \"547071e5-8e57-441b-92b3-d87d9618c6ff\",\n"
				+ "    \"taskName\" : \"Rabbit test task 3\",\n" + "    \"status\" : 16,\n"
				+ "    \"dateUpdated\" : 1528050462158,\n" + "    \"number\" : 3\n" + "  }, {\n"
				+ "    \"submissionId\" : \"0e0deaab-8786-4077-a7e4-8a4ee7828e48\",\n" + "    \"evaluatorId\" : null,\n"
				+ "    \"taskId\" : \"19762a23-4d17-40f3-a7a8-ee1a88af91e6\",\n"
				+ "    \"taskName\" : \"Rabbit test task 9\",\n" + "    \"status\" : 16,\n"
				+ "    \"dateUpdated\" : 1528050496113,\n" + "    \"number\" : 9\n" + "  }, {\n"
				+ "    \"submissionId\" : \"42baaafe-2feb-432d-afcb-1efddcd5b518\",\n" + "    \"evaluatorId\" : null,\n"
				+ "    \"taskId\" : \"45d3b82b-2003-4477-bf36-2cecbef0f2a4\",\n"
				+ "    \"taskName\" : \"Rabbit test task 6\",\n" + "    \"status\" : 16,\n"
				+ "    \"dateUpdated\" : 1528050531036,\n" + "    \"number\" : 6\n" + "  }, {\n"
				+ "    \"submissionId\" : \"d749afa3-ccff-469d-adc6-1c4f4e17c15c\",\n" + "    \"evaluatorId\" : null,\n"
				+ "    \"taskId\" : \"fc433b7d-2332-40f9-ad52-cdeda0ea112b\",\n"
				+ "    \"taskName\" : \"Rabbit test task 7\",\n" + "    \"status\" : 16,\n"
				+ "    \"dateUpdated\" : 1528050565498,\n" + "    \"number\" : 7\n" + "  }, {\n"
				+ "    \"submissionId\" : \"454fd6bd-3ac1-47ee-ad19-c85d1e4e97e2\",\n" + "    \"evaluatorId\" : null,\n"
				+ "    \"taskId\" : \"1d72a452-07fc-4216-a262-344cf9d435dd\",\n"
				+ "    \"taskName\" : \"Rabbit test task 5\",\n" + "    \"status\" : 16,\n"
				+ "    \"dateUpdated\" : 1528050600026,\n" + "    \"number\" : 5\n" + "  }, {\n"
				+ "    \"submissionId\" : \"666d5085-2f5f-4293-ad3e-2cd4bda9b995\",\n" + "    \"evaluatorId\" : null,\n"
				+ "    \"taskId\" : \"c7283b79-8658-47a5-8f37-ba4fa9ba827e\",\n"
				+ "    \"taskName\" : \"Rabbit test task 10\",\n" + "    \"status\" : 16,\n"
				+ "    \"dateUpdated\" : 1528050634386,\n" + "    \"number\" : 10\n" + "  }, {\n"
				+ "    \"submissionId\" : \"adf41060-202b-4284-95b2-738499b7e9b3\",\n" + "    \"evaluatorId\" : null,\n"
				+ "    \"taskId\" : \"6b9c12a9-3085-4429-8327-501aa04e4723\",\n"
				+ "    \"taskName\" : \"Rabbit test task 8\",\n" + "    \"status\" : 16,\n"
				+ "    \"dateUpdated\" : 1528050668445,\n" + "    \"number\" : 8\n" + "  }, {\n"
				+ "    \"submissionId\" : \"d5db277b-52f0-41bd-b220-f20e5e6ed6f8\",\n" + "    \"evaluatorId\" : null,\n"
				+ "    \"taskId\" : \"07897c1a-289d-48ac-8481-cee43a86e308\",\n"
				+ "    \"taskName\" : \"Rabbit test task 4\",\n" + "    \"status\" : 16,\n"
				+ "    \"dateUpdated\" : 1528050702343,\n" + "    \"number\" : 4\n" + "  }, {\n"
				+ "    \"submissionId\" : \"0e76e10b-4a23-43a3-a453-e288177d54e5\",\n"
				+ "    \"evaluatorId\" : \"E00107448\",\n"
				+ "    \"taskId\" : \"ca0ff4cc-b610-4cd3-b98d-03283be0d667\",\n"
				+ "    \"taskName\" : \"Rabbit test task 2\",\n" + "    \"status\" : 16,\n"
				+ "    \"dateUpdated\" : 1528131337584,\n" + "    \"number\" : 0\n" + "  } ]\n" + "}\n"
				+ "2018-06-04 16:55:37.601 DEBUG 3246 --- [nio-8274-exec-9] e.w.d.service.NotificationService        : NotificationService.notify() sent fail to Academic Activity.";

		final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE | Pattern.DOTALL);
		final Matcher matcher = pattern.matcher(string);

		while (matcher.find()) {
			System.out.println("Full match: " + matcher.group(0));
			for (int i = 1; i <= matcher.groupCount(); i++) {
				System.out.println("Group " + i + ": " + matcher.group(i));
			}
		}

	}
	
	public static void main(String[] args) throws IOException {
	//	extractFinishedProcessingMsgs();
	//	extractAssessmentModelsSent();
		extractCombined();
	}
}
