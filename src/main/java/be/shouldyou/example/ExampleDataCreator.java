package be.shouldyou.example;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.IntStream;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.springframework.stereotype.Component;

import be.shouldyou.example.entity.Submission;
import be.shouldyou.example.repo.SubmissionRepo;

@Component
public class ExampleDataCreator {
	@Inject
	private SubmissionRepo submissionRepo;
	
	@PostConstruct
	public void setup(){
		SimpleDateFormat format = new SimpleDateFormat("yyyyDDD");
		IntStream.range(0, 100).forEach(num ->{
			Submission s = new Submission();
			s.setXmlData("<abc></abc>");
			s.setCreatedDate(new Date());
			
			String julian = format.format(new Date());
			String submissionId = String.format("98765%s%07d", julian, num);
			s.setSubmissionId(submissionId);
			
			submissionRepo.save(s);
		});
	}
}
