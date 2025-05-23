package com.ssilvadev.api.data.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import com.ssilvadev.api.model.Book;

@Relation(collectionRelation = "books")
public class BookDTO extends RepresentationModel<BookDTO> implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private Long id;
    private String author;
    private Date launchDate;
    private Double price;
    private String title;

    public BookDTO(){
        
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getLaunchDate() {
        return launchDate;
    }

    public void setLaunchDate(Date launchDate) {
        this.launchDate = launchDate;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(getId(), getAuthor(), getLaunchDate(), getPrice(), getTitle());
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Book book))
            return false;

        return Objects.equals(getId(), book.getId()) &&
                Objects.equals(getAuthor(), book.getAuthor()) &&
                Objects.equals(getLaunchDate(), book.getLaunchDate()) &&
                Objects.equals(getPrice(), book.getPrice()) &&
                Objects.equals(getTitle(), book.getTitle());
    }
}
