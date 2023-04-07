package com.jumpjump.bot.listener;

import com.jumpjump.bot.service.GroupService;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.contact.MemberPermission;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.contact.active.MemberActive;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Component
public class GroupMassage extends SimpleListenerHost {


    @Resource
    private GroupService groupService;
    @EventHandler
    public void onMessage(@NotNull GroupMessageEvent event) throws Exception { // 可以抛出任何异常, 将在 handleException 处理
        String s = event.getMessage().contentToString();
        if(GroupService.session.containsKey(event.getSender().getId())){
            if(s.equals("取消")){
                GroupService.session.remove(event.getSender().getId());
                GroupService.step=0;
                event.getSubject().sendMessage("已取消hmd的录入,可以重新输入lr命令进行hmd的录入");
                return;
            }
            switch (GroupService.step){
                case 1:
                    groupService.step1(event);
                    return;
                case 2:
                    groupService.step2(event);
                    return;
                case 3:
                    groupService.step3(event);
                    return;
                case 4:
                    groupService.step4(event);
                    return;
                case 5:
                    groupService.step5(event);
                    return;
                case 6:
                    groupService.step6(event);
                    return;
            }
        }
        if(s.equals("lr")){
            if(event.getGroup().getId()==865370554){
                groupService.hmd(event);
            }
        }else if(s.contains("bhd=")){
            groupService.select(event);
        }
    }
}
