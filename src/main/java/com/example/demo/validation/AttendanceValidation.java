package com.example.demo.validation;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import com.example.demo.inter.MessageOutput;
import com.example.demo.model.AttendanceFormList;

@Component
public class AttendanceValidation {
	
	 private final MessageOutput messageOutput; 
	  
	  public AttendanceValidation(MessageOutput messageOutput){
		  this.messageOutput =messageOutput;
	  }
	
	public void errorCheck(AttendanceFormList attendanceFormList, BindingResult result) {
		for (int i = 0; i < attendanceFormList.getAttendanceList().size(); i++) {
			String remarks = attendanceFormList.getAttendanceList().get(i).getAttendanceRemarks();
			String startTime = attendanceFormList.getAttendanceList().get(i).getStartTime();
			String endTime = attendanceFormList.getAttendanceList().get(i).getEndTime();
			Integer status = attendanceFormList.getAttendanceList().get(i).getStatus();
			List<Integer> holiday = Arrays.asList(1, 2, 4, 5, 9, 11, 12);
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

			if (remarks.length() > 20) {
				FieldError attendanceRemarks = new FieldError("attendanceFormList",
						"attendanceList[" + i + "].attendanceRemarks", messageOutput.message("RemarksOver"));
				result.addError(attendanceRemarks);
			}

			if (!remarks.isEmpty() && remarks.matches("^[a-zA-Z0-9!-/:-@\\[-`{-~]*$")) {
				FieldError attendanceRemarks = new FieldError("attendanceFormList",
						"attendanceList[" + i + "].attendanceRemarks", messageOutput.message("RemarkeZennkaku"));
				result.addError(attendanceRemarks);
			}

			if (!startTime.isEmpty()) {
				try {
					if (holiday.contains(status)) {
						FieldError itemInaccurate = new FieldError("attendanceFormList", "itemInaccurate",
								messageOutput.message("itemInaccurate"));
						result.addError(itemInaccurate);
					}
					LocalTime.parse(startTime, formatter);
				} catch (DateTimeParseException e) {
					FieldError timeFormatError = new FieldError("attendanceFormList",
							"attendanceList[" + i + "].startTime", messageOutput.message("timeFormat"));
					result.addError(timeFormatError);
				}
			}

			if (!endTime.isEmpty()) {
				try {
					if (holiday.contains(status)) {
						FieldError itemInaccurate = new FieldError("attendanceFormList", "itemInaccurate",
								messageOutput.message("itemInaccurate"));
						result.addError(itemInaccurate);
					}
					LocalTime endInputTime = LocalTime.parse(endTime, formatter);
					if (!startTime.isEmpty()) {
						LocalTime startInputTime = LocalTime.parse(startTime, formatter);
						if (endInputTime.isBefore(startInputTime)) {
							FieldError startEndTime = new FieldError("attendanceFormList",
									"attendanceList[" + i + "].endTime", messageOutput.message("consistency"));
							result.addError(startEndTime);
						}
					}
				} catch (DateTimeParseException e) {
					FieldError timeFormatError = new FieldError("attendanceFormList",
							"attendanceList[" + i + "].endTime", messageOutput.message("timeFormat"));
					result.addError(timeFormatError);
				}
			}
		}
	}
}
