package com.changyu.po;

import lombok.Data;
import lombok.extern.jbosslog.JBossLog;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity(name = "t_blog")
@Table
public class Blog {

    @Id
    @GeneratedValue
    private Long id;

    private String title;

    @Basic(fetch = FetchType.LAZY)
    @Lob
    private String content;

    private String topPicture;

    private String flag;

    private Integer viewCount;

    private Boolean appreciationEnable;

    private Boolean shareStatementEnable;

    private Boolean commentEnable;

    private Boolean published;

    private Boolean recommend;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;

    @ManyToOne
    private Type type;

    @ManyToMany(cascade = {CascadeType.PERSIST})
    private List<Tag> tags = new ArrayList<>();

    @ManyToOne
    private User user;

    @OneToMany(mappedBy = "blog")
    private List<Comment> comments = new ArrayList<>();

}
