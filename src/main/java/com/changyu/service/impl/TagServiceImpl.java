package com.changyu.service.impl;

import com.changyu.dao.TagRepository;
import com.changyu.exception.NotFoundException;
import com.changyu.po.Tag;
import com.changyu.po.Type;
import com.changyu.service.TagService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TagServiceImpl implements TagService {

    @Autowired
    private TagRepository tagRepository;

    @Transactional
    @Override
    public Tag saveTag(Tag tag) {
        return tagRepository.save(tag);
    }

    @Override
    public Tag getTag(Long id) {
        return tagRepository.findById(id).get();
    }

    @Override
    public Tag getTagByName(String name) {
        return tagRepository.findByName(name);
    }

    @Override
    public Page<Tag> listTag(Pageable pageable) {
        return tagRepository.findAll(pageable);
    }

    @Override
    public List<Tag> listTag() {
        return tagRepository.findAll();
    }

    @Override
    public List<Tag> listTag(String idList) {
        return tagRepository.findAllById(convertStrToList(idList));
    }

    @Transactional
    @Override
    public Tag updateTag(Long id, Tag tag) {
        Optional<Tag> OptionalTag = tagRepository.findById(id);
        if (!OptionalTag.isPresent()) {
            throw new NotFoundException("不存在该类型");
        }
        Tag updateTag = OptionalTag.get();
        BeanUtils.copyProperties(tag, updateTag);
        return tagRepository.save(updateTag);
    }

    @Transactional
    @Override
    public void deleteTag(Long id) {
        tagRepository.deleteById(id);
    }

    private List<Long> convertStrToList(String str) {
        List<Long> result = new ArrayList<>();
        if("".equals(str) || str == null){
            return result;
        }
        String[] strList = str.split(",");
        for(String idStr : strList) {
            result.add(new Long(idStr));
        }
        return result;
    }
}