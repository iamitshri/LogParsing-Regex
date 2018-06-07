package com.example.logparser;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Iterables;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class LogparserApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(LogparserApplication.class, args);
	}

	String readFile(String fileName, String filePath) throws IOException {
		Path path = Paths.get(filePath, fileName);
		String str = new String(Files.readAllBytes(path));
		return str;
	}

	public void extractAssessmentModelsSent(String fileName, String filePath) throws IOException {
		// read the file in string
		String file = readFile(fileName, filePath);

		String regex1 = getPatternForExtractingAssessmentModels();
		String regex2 = getRegexForFinishedProcessingSubmissions();
		final Pattern pattern = Pattern.compile(regex1 + "|" + regex2, Pattern.MULTILINE);
		Matcher matcher = pattern.matcher(file);

		ObjectMapper mapper = new ObjectMapper();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
		List<LogModel> logModels = new ArrayList<>();
		List<FinishedProcessing> retryLaterList = new ArrayList<>();
		while (matcher.find()) {
			if (matcher.group(1) != null) {
				String dateOfLogEntry = matcher.group(1);
				LocalDateTime dateTimeofLog = LocalDateTime.parse(dateOfLogEntry, formatter);
				AssessmentModel asmtModel = mapper.readValue(matcher.group(2), AssessmentModel.class);
				LogModel logModel = new LogModel(dateTimeofLog, asmtModel);
				logModels.add(logModel);
			} else {
				// Date of Processing finished message
				String finishedProcessingEntry = matcher.group(4);
				LocalDateTime dateTimefinishedProcessing = LocalDateTime.parse(finishedProcessingEntry, formatter);
				// Submission Id
				String submissionId = StringUtils.trimWhitespace(matcher.group(5));
				// Status
				String status = matcher.group(6);
				boolean matched = false;
				for (LogModel lm2 : logModels) {
					TaskModel tm = Iterables.getLast(lm2.getModel().getTasks());
					if (CheckIfSubmissionIdStatusMatches(submissionId, status, tm)) {
						tm.setFinishedProcessingTime(dateTimefinishedProcessing);
						matched = true;
					}
				}
				if (!matched) {
					retryLaterList.add(new FinishedProcessing(dateTimefinishedProcessing, submissionId, status));
				}
			}
		}
		System.out.println("size of retryList:" + retryLaterList.size());
		runThroughRetryList(logModels, retryLaterList);
		int cntOfMissing = 1;
		for (int i = 0; i < logModels.size(); i++) {
			// Get the last Assessment Model that has matching submissionID and status
			AssessmentModel assmntModel = logModels.get(i).getModel();
			TaskModel tm = Iterables.getLast(assmntModel.getTasks());
			if (tm.getFinishedProcessingTime() == null) {
				System.out.println((cntOfMissing++) + " : No Finished Processing Time for AssessmentModel at time:"
						+ logModels.get(i).getDateOfLogEntry() + " submissionId: "
						+tm.getSubmissionId()+", status:"+tm.getStatus());
			}
		}

		writeExcel(logModels, "/Users/amit.shrigondekar/logs/excels", fileName + "-excel-file-");

	}

	void runThroughRetryList(List<LogModel> logModels, List<FinishedProcessing> retryLaterList) {
		System.out.println("size of retryList:" + retryLaterList.size());
		// Get the last Assessment Model that has matching submissionID and status
		
		for (FinishedProcessing fp : retryLaterList) {
			boolean matched=false;
			for (LogModel lm2 : logModels) {
				TaskModel tm = Iterables.getLast(lm2.getModel().getTasks());
				if (CheckIfSubmissionIdStatusMatches(fp, tm)) {
					tm.setFinishedProcessingTime(fp.getDateTimefinishedProcessing());
					matched=true;
					
				}
			}
			if(!matched) {
					System.out.println("submissionId: "+ fp.getSubmissionId()+", status: "+fp.getStatus()+", date: "+fp.getDateTimefinishedProcessing());
 
			}
		}
	}

	 

	private boolean CheckIfSubmissionIdStatusMatches(FinishedProcessing fp, TaskModel tm) {
		return CheckIfSubmissionIdStatusMatches(fp.getSubmissionId(),fp.getStatus(),tm);
	}

	private boolean CheckIfSubmissionIdStatusMatches(String submissionId, String status, TaskModel tm) {
		return (tm.getSubmissionId() != null && tm.getSubmissionId().equals(submissionId))
				&& tm.getStatus() == Integer.parseInt(status);
	}

	private String getRegexForFinishedProcessingSubmissions() {
		final String regex = "^(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d+).*?NotificationService.sendToDreamCatcher\\(\\): Finished processing.*?submissionId:(.*)?,\\sstatus:\\s(-?\\d+)$";
		return regex;
	}

	private String getPatternForExtractingAssessmentModels() {
		final String regex = "^(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d+).*?NotificationService.sendAssessment\\(\\) sending to Dream Catcher:((.|\n)*?)\\d{4}-\\d{2}-\\d{2}";
		return regex;
	}

	@Override
	public void run(String... args) throws Exception {
		extractAssessmentModelsSent("combined-6-6.log", "/Users/amit.shrigondekar/logs/6-6/");

	}

	public void writeExcel(List<LogModel> logModels, String excelFilePath, String fn) throws IOException {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");

		String formatDateTime = LocalDateTime.now().format(formatter);

		String fileName = excelFilePath + "/log" + "-" + fn + "-" + formatDateTime + ".xls";
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("LogAnalysis-DM");

		// dateOfLogEntry, studentId, assessmentCode, submissionId, taskId, taskName,
		// status, dateUpdated <- from the TaskModel record

		HSSFRow rowhead = sheet.createRow((short) 0);
		rowhead.createCell(0).setCellValue("dateOfLogEntry");
		rowhead.createCell(1).setCellValue("studentId");
		rowhead.createCell(2).setCellValue("assessmentCode");
		rowhead.createCell(3).setCellValue("submissionId");
		rowhead.createCell(4).setCellValue("taskId");
		rowhead.createCell(5).setCellValue("taskName");
		rowhead.createCell(6).setCellValue("status");
		rowhead.createCell(7).setCellValue("dateUpdatedFromTaskModel");
		rowhead.createCell(8).setCellValue("dateFinishedProcessingTime");

		int rowCount = 0;

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

		for (LogModel lm : logModels) {

			TaskModel tm = Iterables.getLast(lm.getModel().getTasks());

			Row row = sheet.createRow(++rowCount);

			Cell cell = row.createCell(0);
			cell.setCellValue("" + lm.getDateOfLogEntry());

			cell = row.createCell(1);
			cell.setCellValue("" + lm.getModel().getStudentId());

			cell = row.createCell(2);
			cell.setCellValue("" + lm.getModel().getAssessmentCode());

			cell = row.createCell(3);
			cell.setCellValue("" + tm.getSubmissionId());

			cell = row.createCell(4);
			cell.setCellValue(tm.getTaskId());

			cell = row.createCell(5);
			cell.setCellValue(tm.getTaskName());

			cell = row.createCell(6);
			cell.setCellValue(tm.getStatus());

			cell = row.createCell(7);
			String reportDate = df.format(tm.getDateUpdated());
			cell.setCellValue(reportDate);

			cell = row.createCell(8);
			String finishedProcessingTime = "";
			if (tm.getFinishedProcessingTime() != null) {
				finishedProcessingTime = tm.getFinishedProcessingTime().toString();
			}
			cell.setCellValue(finishedProcessingTime);

		}
		try (FileOutputStream outputStream = new FileOutputStream(fileName)) {
			workbook.write(outputStream);
		}
		workbook.close();
	}

}
