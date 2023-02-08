package com.swordintent.chatgpt;

import com.swordintent.chatgpt.protocol.ChatGptConfig;
import com.swordintent.chatgpt.protocol.ChatResponse;
import org.junit.Test;
import com.swordintent.chatgpt.protocol.ChatRequest;
import com.swordintent.chatgpt.utils.JsonUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void test() throws Exception {
        ChatGptConfig chatGptConfig = ChatGptConfig.builder()
//                .proxy("http://192.168.50.254:9853")
//                .email("")
                .password("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")
//                .authorization("")
                .build();
        ChatgptClientImpl client = ChatgptClientImpl.getInstance();
        client.init("http://192.168.1.9:5000", chatGptConfig);
        List<String> strings = Arrays.asList("你好", "我需要摄入多少蛋白质才能获得肌肉质量");
        int size = strings.size();
        int threadNum = 1;
        int slice = size / threadNum;

        for(int i = 0; i < threadNum; i ++){
            List<String> subList = strings.subList(slice * i, slice * (i + 1));
            makeThreadRequest(client, subList);
        }
        Thread.sleep(60000L);
    }

    private void makeThreadRequest(ChatgptClientImpl client, List<String> subList) {
        new Thread(() ->{
            String conversationId = null;
            String parentId = null;
            for(String str : subList){
                ChatRequest chatRequest = ChatRequest.builder().prompt(str)
                        .conversationId(conversationId)
                        .parentId(parentId)
                        .build();
                try {
                    ChatResponse chat = client.chat(chatRequest);
                    System.out.println(JsonUtils.toJson(chat));
                    conversationId = chat.getConversationId();
                    parentId = chat.getParentId();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
}
