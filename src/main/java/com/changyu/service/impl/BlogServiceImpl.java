package com.changyu.service.impl;

import com.changyu.dao.BlogRepository;
import com.changyu.exception.NotFoundException;
import com.changyu.po.Blog;
import com.changyu.po.Type;
import com.changyu.service.BlogService;
import com.changyu.util.MarkdownUtils;
import com.changyu.util.MyBeanUtils;
import com.changyu.vo.BlogQuery;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import java.util.*;

@Service
public class BlogServiceImpl implements BlogService {

    @Autowired
    private BlogRepository blogRepository;

    @Override
    public Blog getBlog(Long id) {
        return blogRepository.findById(id).get();
    }

    @Transactional
    @Override
    public Blog getAndConvert(Long id) {
        Optional<Blog> OptionalBlog = blogRepository.findById(id);
        if(!OptionalBlog.isPresent()) {
            throw new NotFoundException("该博客不存在");
        }
        Blog blog = OptionalBlog.get();
        Blog tempBlog = new Blog();
        BeanUtils.copyProperties(blog, tempBlog);
        tempBlog.setContent(MarkdownUtils.markdownToHtmlExtensions(tempBlog.getContent()));

        blogRepository.updateViewCount(id);
        return tempBlog;
    }

    @Override
    public Page<Blog> listBlog(@PageableDefault(size = 5, sort = {"updateTime"}, direction = Sort.Direction.DESC) Pageable pageable, BlogQuery blog) {
        return blogRepository.findAll(new Specification<Blog>() {
            @Override
            public Predicate toPredicate(Root<Blog> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList();
                if(!"".equals(blog.getTitle()) && blog.getTitle() != null) {
                    predicates.add(criteriaBuilder.like(root.<String>get("title"), "%"+blog.getTitle()+"%"));
                }
                if(blog.getTypeId() != null) {
                    predicates.add(criteriaBuilder.equal(root.<Type>get("type").get("id"), blog.getTypeId()));
                }
                if(blog.getRecommend() != null) {
                    predicates.add(criteriaBuilder.equal(root.<Boolean>get("recommend"), blog.getRecommend()));
                }
                criteriaQuery.where(predicates.toArray(new Predicate[predicates.size()]));
                return null;
            }
        }, pageable);
    }

    @Override
    public Page<Blog> listBlog(Pageable pageable) {
        return blogRepository.findAll(pageable);
    }

    @Override
    public Page<Blog> listBlog(Long tagId, Pageable pageable) {

        return blogRepository.findAll(new Specification<Blog>() {
            @Override
            public Predicate toPredicate(Root<Blog> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Join join = root.join("tags");
                return criteriaBuilder.equal(join.get("id"), tagId);
            }
        }, pageable);
    }

    @Override
    public Page<Blog> listBlog(Pageable pageable, String query) {
        return blogRepository.findByQuery(pageable, query);
    }

    @Override
    public List<Blog> listRecommendBlogTop(Integer size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "updateTime");
        Pageable pageable = PageRequest.of(0, size, sort);
        return blogRepository.findTop(pageable);
    }

    @Override
    public Map<String, List<Blog>> archiveBlog() {
        List<String> years = blogRepository.findGroupYear();
        Map<String, List<Blog>> map = new TreeMap<>();
        for(String year : years) {
            map.put(year, blogRepository.findByYear(year));
        }
        return map;
    }

    @Override
    public Long countBlog() {
        return blogRepository.count();
    }

    @Transactional
    @Override
    public Blog saveBlog(Blog blog) {
        if(blog.getId() == null) {
            blog.setCreateTime(new Date());
            blog.setUpdateTime(new Date());
            blog.setViewCount(0);
        } else {
            blog.setUpdateTime(new Date());
        }

        return blogRepository.save(blog);
    }

    @Transactional
    @Override
    public Blog updateBlog(Long id, Blog blog) {
        Optional<Blog> OptionalBlog = blogRepository.findById(id);
        if(!OptionalBlog.isPresent()) {
            throw new NotFoundException("该博客不存在");
        }
        Blog updateBlog = OptionalBlog.get();
        updateBlog.setUpdateTime(new Date());

        // checkbox若不选中传入null值
        if(blog.getAppreciationEnable() == null) {
            blog.setAppreciationEnable(false);
        }
        if(blog.getCommentEnable() == null) {
            blog.setCommentEnable(false);
        }
        if(blog.getShareStatementEnable() == null) {
            blog.setShareStatementEnable(false);
        }
        if(blog.getRecommend() == null) {
            blog.setRecommend(false);
        }

        BeanUtils.copyProperties(blog, updateBlog, MyBeanUtils.getNullPropertyNames(blog));
        return blogRepository.save(updateBlog);
    }

    @Transactional
    @Override
    public void deleteBlog(Long id) {
        blogRepository.deleteById(id);
    }
}
