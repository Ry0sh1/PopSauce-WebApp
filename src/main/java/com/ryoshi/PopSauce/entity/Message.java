package com.ryoshi.PopSauce.entity;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Message {

    private String gameCode;
    private String sender;
    private Object content;
    private MessageType messageType;

}
