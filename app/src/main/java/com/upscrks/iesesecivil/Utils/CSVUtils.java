package com.upscrks.iesesecivil.Utils;

import android.content.Context;
import android.util.Log;

import com.opencsv.CSVReader;
import com.upscrks.iesesecivil.Application.Helper;
import com.upscrks.iesesecivil.Database.DataAccess;
import com.upscrks.iesesecivil.Database.Model.Books;
import com.upscrks.iesesecivil.Database.Model.MCQ;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CSVUtils {

    public static void addMCQs(Context context) {
        DataAccess mDataAccess = DataAccess.getInstance(context);
        mDataAccess.getCsv(csvData -> {
            try {
                CSVReader csvReader = new CSVReader(new StringReader(csvData));
                String[] nextLine;
                List<MCQ> list = new ArrayList<>();
                int questionId = 1;
                while (true) {
                    if (!((nextLine = csvReader.readNext()) != null)) break;
                    // nextLine[] is an array of values from the line
                    MCQ question = new MCQ();
                    //question.setQuestionId(Helper.md5(Helper.currentDate()));
                    question.setQuestion(nextLine[0]);
                    question.setOption1(nextLine[1]);
                    question.setOption2(nextLine[2]);
                    question.setOption3(nextLine[3]);
                    question.setOption4(nextLine[4]);
                    if ("A".equals(nextLine[5]))
                        question.setCorrectAnswer(1);
                    else if ("B".equals(nextLine[5]))
                        question.setCorrectAnswer(2);
                    else if ("C".equals(nextLine[5]))
                        question.setCorrectAnswer(3);
                    else if ("D".equals(nextLine[5]))
                        question.setCorrectAnswer(4);
                    question.setSubject(nextLine[6]);
                    question.setPrevYear(true);
                    question.setCreatedOn(Calendar.getInstance().getTimeInMillis());
                    list.add(question);
                    mDataAccess.addMCQ(question,  onComplete -> {
                    });
                    Log.d("CSV Utils", question.toString() +" id= "+questionId);
                    questionId++;
                    Thread.sleep(100);
                }
                Log.d("CSV List", "addMcq: Number of Questions =  " + list.size());
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    public static void addBooks(Context context) {
        DataAccess mDataAccess = DataAccess.getInstance(context);
        mDataAccess.getCsv(csvData -> {
            try {
                CSVReader csvReader = new CSVReader(new StringReader(csvData));
                String[] nextLine;
                List<Books> list = new ArrayList<>();
                int bookId = 1;
                while (true) {
                    if (!((nextLine = csvReader.readNext()) != null)) break;
                    Books book = new Books();
                    book.setSubject(nextLine[0]);
                    book.setBookAuthor(nextLine[1]);
                    book.setBookTitle(nextLine[2]);
                    book.setLink(nextLine[3]);
                    list.add(book);
                    mDataAccess.addBook(book,  onComplete -> {
                    });
                    Log.d("CSV Utils", book.getBookTitle() +" id= "+bookId);
                    bookId++;
                    Thread.sleep(300);
                }
                Log.d("CSV List", "addMcq: Number of Books =  " + list.size());
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
}
