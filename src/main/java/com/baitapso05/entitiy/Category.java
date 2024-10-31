package com.baitapso05.entitiy;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name="categories")
@NamedQuery(name = "Category.findAll", query = "SELECT c FROM Category c")
public class Category implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CategoryId")
    private int categoryId;

    @Column(name = "CategoryName", columnDefinition = "NVARCHAR(200) NOT NULL")
    @NotEmpty(message = "You must have a name for Category")
    private String categoryName;

    @Column(name = "Images", columnDefinition = "NVARCHAR(500) NULL")
    private String images;

    @Column(name = "Status")
    private int status;

//    @OneToMany(mappedBy = "category")
//    private List<Video> videos;
//
//
//    public Video addVideo(Video video) {
//        getVideos().add(video);
//        video.setCategory(this);
//        return video;
//    }
//
//    public Video removeVideo(Video video) {
//        getVideos().remove(video);
//        video.setCategory(null);
//        return video;
//    }


    @Override
    public String toString() {
        return "Category{" +
                "categoryId=" + categoryId +
                ", categoryName='" + categoryName + '\'' +
                ", images='" + images + '\'' +
                ", status=" + status +
                '}';
    }
}
