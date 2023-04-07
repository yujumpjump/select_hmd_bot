package com.jumpjump.bot.service;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jumpjump.bot.mapper.HmdMapper;
import com.jumpjump.bot.model.Hmd;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.event.events.MessageEvent;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GroupService {

    @Resource
    private HmdMapper hmdMapper;

    @Resource
    private RestTemplate restTemplate;
    public static Map<Long, Hmd> session = new ConcurrentHashMap();

    public  static  int step = 0;

    public void hmd(MessageEvent event){
        User sender = event.getSender();
        long id = sender.getId();
        session.put(id, new Hmd());
        event.getSubject().sendMessage("输入被举报人id,可随时输入取消命令取消录入!");
        ++step;
    }

    public void step1(MessageEvent event){
        Hmd newHmd = session.get(event.getSender().getId());
        newHmd.setName(event.getMessage().contentToString());
        String name = newHmd.getName();
        String url = url(name);
        String body = getUid(url);
        if(body==null){
            event.getSubject().sendMessage("在api中无查询到用户uid请输入正确游戏id!");
            step=1;
            return;
        }
        String userId = JSONObject.parseObject(body).getString("userId");
        newHmd.setId(userId);
        event.getSubject().sendMessage("被举报原因");
        ++step;
    }
    public void step2(MessageEvent event){
        Hmd hmd = session.get(event.getSender().getId());
        hmd.setLy(event.getMessage().contentToString());
        event.getSubject().sendMessage("处理服务器");
        ++step;
    }
    public void step3(MessageEvent event){
        Hmd hmd = session.get(event.getSender().getId());
        hmd.setServer(event.getMessage().contentToString());
        event.getSubject().sendMessage("被举报时间");
        ++step;
    }

    public void step4(MessageEvent event){
        Hmd hmd = session.get(event.getSender().getId());
        hmd.setTime(event.getMessage().contentToString());
        event.getSubject().sendMessage("处理人");
        ++step;
    }

    public void step5(MessageEvent event){
        String regex = "^\\w+$";

        if(!event.getMessage().contentToString().matches(regex)){
            step=5;
            event.getSubject().sendMessage("必须输入处理人的游戏id!");
            return;
        }
        Hmd hmd = session.get(event.getSender().getId());
        hmd.setClrid(event.getMessage().contentToString());
        event.getSubject().sendMessage("证据链接");
        ++step;
    }
    public void step6(MessageEvent event){
        Hmd newHmd = session.get(event.getSender().getId());
        newHmd.setUrl(event.getMessage().contentToString());
        Hmd odeHmd = hmdMapper.selectById(newHmd.getId());
        if(odeHmd==null){
            if(hmdMapper.insert(newHmd)==1){
                event.getSubject().sendMessage("hmd录入完成");
                session.remove(event.getSender().getId());
                step=0;
                return;
            }
        }
        event.getSubject().sendMessage(
                "此id在hmd中已有记录!,上一条记录如下"+ "\n"+
                "pid: "+ odeHmd.getId()+"\n"+
                "newName: "+ newHmd.getName()+"\n"+
                "oidName: "+odeHmd.getName()+"\n"+
                "理由: "+odeHmd.getLy()+"\n"+
                "服务器: "+ odeHmd.getServer()+"\n"+
                "被举报时间: "+ odeHmd.getTime()+"\n"+
                "录入时间: "+odeHmd.getLrtime()+"\n"+
                "管理员id: "+odeHmd.getClrid()+"\n"+
                "被黑次数: "+odeHmd.getSun()+"\n"+
                "证据链接: "+odeHmd.getUrl()+"\n"+
                "数据已覆盖，但证据链接不会覆盖!");
        session.remove(event.getSender().getId());
        newHmd.setSun(odeHmd.getSun()+1);
        newHmd.setUrl(odeHmd.getUrl()+"----"+newHmd.getUrl());
        hmdMapper.updateById(newHmd);
        step=0;
    }


    public void select(MessageEvent event){
        String s = event.getMessage().contentToString();
        String[] split = s.split("=");
        Hmd hmd = hmdMapper.selectOne(new QueryWrapper<Hmd>().eq("name",split[1]));
        if(hmd==null){
            event.getSubject().sendMessage("黑名单中没有存在此id,请继续保持");
            return;
        }
        event.getSubject().sendMessage(
                        "id: "+hmd.getName()+"\n"+
                        "理由: "+hmd.getLy()+"\n"+
                        "服务器: "+ hmd.getServer()+"\n"+
                        "被举报时间: "+ hmd.getTime()+"\n"+
                        "录入时间: "+hmd.getLrtime()+"\n"+
                        "管理员id: "+hmd.getClrid()+"\n"+
                        "次数: "+hmd.getSun()+"\n"+
                        "证据链接: "+hmd.getUrl());
    }

    /**
     * 处理url
     * @param name
     * @return
     */
    private String url(String  name){
        String url ="https://api.gametools.network/bfv/stats/?format_values=true&name=user&platform=pc&skip_battlelog=false&lang=zh-cn";
        String user = url.replace("user", name);
        return user;
    }
    
    
    private String getUid(String url){
        String body = null;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("user-agent",
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.97 Safari/537.36");
            HttpEntity<String> res = restTemplate
                    .exchange(url, HttpMethod.GET, new HttpEntity<>(null, headers),
                            String.class);
            body = res.getBody();   
        }catch (Exception e){
            return null;
        }
       return body;
    }

}


