package core;

import entities.TopicEntity;

public class DefaultTopicFormatter implements TopicFormatter {

    // %dl%
    @Override
    public String formatTopic(TopicEntity topic) {
        String topicString = topic.getPattern();
        topicString = topicString.replaceAll("%dl%", 
                Long.toString(topic.remainingDays()));
        return topicString;
    }

}
