package com.lambdaschool.bookstore.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lambdaschool.bookstore.BookstoreApplication;
import com.lambdaschool.bookstore.models.Author;
import com.lambdaschool.bookstore.models.Book;
import com.lambdaschool.bookstore.models.Section;
import com.lambdaschool.bookstore.models.Wrote;
import com.lambdaschool.bookstore.services.BookService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)

/*****
 * Due to security being in place, we have to switch out WebMvcTest for SpringBootTest
 * @WebMvcTest(value = BookController.class)
 */
@SpringBootTest(classes = BookstoreApplication.class)

/****
 * This is the user and roles we will use to test!
 */
//@WebMvcTest(value = BookController.class)
@WithMockUser(username = "admin", roles = {"ADMIN", "DATA"})
public class BookControllerTest
{
    /******
     * WebApplicationContext is needed due to security being in place.
     */
    @Autowired
    private WebApplicationContext webApplicationContext;

//    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    List<Book> bookList = new ArrayList<>();

    @Before
    public void setUp() throws
            Exception
    {
        /*****
         * The following is needed due to security being in place!
         */
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        /*****
         * Note that since we are only testing bookstore data, you only need to mock up bookstore data.
         * You do NOT need to mock up user data. You can. It is not wrong, just extra work.
         */
        bookList = new ArrayList<>();

        bookService.deleteAll();
        Author a1 = new Author("John", "Mitchell");
        Author a2 = new Author("Dan", "Brown");
        Author a3 = new Author("Jerry", "Poe");
        Author a4 = new Author("Wells", "Teague");
        Author a5 = new Author("George", "Gallinger");
        Author a6 = new Author("Ian", "Stewart");

        a1.setAuthorid(1);
        a2.setAuthorid(2);
        a3.setAuthorid(3);
        a4.setAuthorid(4);
        a5.setAuthorid(5);
        a6.setAuthorid(6);

        Section s1 = new Section("Fiction");
        Section s2 = new Section("Technology");
        Section s3 = new Section("Travel");
        Section s4 = new Section("Business");
        Section s5 = new Section("Religion");

        s1.setSectionid(1);
        s2 .setSectionid(2);
        s3.setSectionid(3);
        s4.setSectionid(4);
        s5.setSectionid(5);

        Book b1 = new Book("Flatterland", "9780738206752", 2001, s1);
        b1.getWrotes()
                .add(new Wrote(a6, new Book()));
        b1.setBookid(10);

        Book b2 = new Book("Digital Fortess", "9788489367012", 2007, s1);
        b2.getWrotes()
                .add(new Wrote(a2, new Book()));
        b2.setBookid(20);

        Book b3 = new Book("The Da Vinci Code", "9780307474278", 2009, s1);
        b3.getWrotes()
                .add(new Wrote(a2, new Book()));
        b3.setBookid(30);

        Book b4 = new Book("Essentials of Finance", "1314241651234", 0, s4);
        b4.getWrotes()
                .add(new Wrote(a3, new Book()));
        b4.getWrotes()
                .add(new Wrote(a5, new Book()));
        b4.setBookid(40);

        Book b5 = new Book("Calling Texas Home", "1885171382134", 2000, s3);
        b5.getWrotes()
                .add(new Wrote(a4, new Book()));
        b5.setBookid(50);

        bookList.add(b1);
        bookList.add(b2);
        bookList.add(b3);
        bookList.add(b4);
        bookList.add(b5);
    }

    @After
    public void tearDown() throws
            Exception
    {
    }

    @Test
    public void listAllBooks() throws
            Exception
    {
        String apiUrl = "/books/books";
        Mockito.when(bookService.findAll()).thenReturn(bookList);

        RequestBuilder rb = MockMvcRequestBuilders.get(apiUrl).accept(MediaType.APPLICATION_JSON);
        MvcResult r = mockMvc.perform(rb).andReturn();
        String tr = r.getResponse().getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        String er = mapper.writeValueAsString(bookList);

        assertEquals(er, tr);
    }

    @Test
    public void getBookById() throws
            Exception
    {
        String apiUrl = "/books/book/20";
        Mockito.when(bookService.findBookById(20)).thenReturn(bookList.get(0));

        RequestBuilder rb = MockMvcRequestBuilders.get(apiUrl).accept(MediaType.APPLICATION_JSON);
        MvcResult r = mockMvc.perform(rb).andReturn();
        String tr = r.getResponse().getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        String er = mapper.writeValueAsString(bookList.get(0));

        assertEquals(er, tr);
    }

    @Test
    public void getNoBookById() throws
            Exception
    {
        String apiUrl = "/books/book/15";
        Mockito.when(bookService.findBookById(100)).thenReturn(null);

        RequestBuilder rb = MockMvcRequestBuilders.get(apiUrl).accept(MediaType.APPLICATION_JSON);
        MvcResult r = mockMvc.perform(rb).andReturn();
        String tr = r.getResponse().getContentAsString();

        String er = "";

        assertEquals(er, tr);
    }

    @Test
    public void addNewBook() throws
            Exception
    {
        String apiUrl = "/books/book";

        Section s2 = new Section("Fiction");
        s2.setSectionid(1);

        Book b2 = new Book("Calling Texas Home", "1885171382134", 2000, s2);
        b2.setBookid(10);

        Author a2 = new Author("Edgar", "Poe");
        a2.setAuthorid(20);

        b2.getWrotes().add(new Wrote(a2, b2));

        ObjectMapper mapper = new ObjectMapper();
        String bookString = mapper.writeValueAsString(b2);

        Mockito.when(bookService.save(any(Book.class))).thenReturn(b2);

        RequestBuilder rb = MockMvcRequestBuilders.post(apiUrl)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookString);

        mockMvc.perform(rb).andExpect(status().isCreated()).andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void updateFullBook() throws Exception
    {
        String apiUrl = "/books/book/{bookid}";

        Book b1 = new Book();
        b1.setBookid(10);
        b1.setTitle("Title");
        b1.setIsbn("1885171382134");
        b1.setCopy(2020);

        Mockito.when(bookService.update(b1, 10))
                .thenReturn(bookList.get(0));

        ObjectMapper mapper = new ObjectMapper();
        String userString = mapper.writeValueAsString(b1);

        RequestBuilder rb = MockMvcRequestBuilders.put(apiUrl, 100L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(userString);

        mockMvc.perform(rb)
                .andExpect(status().is2xxSuccessful())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void deleteBookById() throws
            Exception
    {
        String apiUrl = "/books/book/{bookid}";

        RequestBuilder rb = MockMvcRequestBuilders.delete(apiUrl, "3")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
        mockMvc.perform(rb)
                .andExpect(status().is2xxSuccessful())
                .andDo(MockMvcResultHandlers.print());

    }
}