package com.vet24.service.user;

import com.vet24.models.user.Topic;
import com.vet24.service.ReadWriteService;

import java.util.List;

public interface TopicService extends ReadWriteService<Long, Topic> {

    List<Topic> getTopicByClientId(Long id);

    Topic getTopicWithCommentsById(Long topicId);
}
