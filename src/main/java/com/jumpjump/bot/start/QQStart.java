package com.jumpjump.bot.start;


import com.jumpjump.bot.listener.GroupMassage;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.auth.BotAuthorization;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.utils.BotConfiguration;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class QQStart implements ApplicationRunner {
    @Resource
    private     GroupMassage groupMassage;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        // Java 2315200674 3342522596
        Bot bot = BotFactory.INSTANCE.newBot(3342522596L, BotAuthorization.byQRCode(), new BotConfiguration() {{
            // 配置，例如：
            fileBasedDeviceInfo();
            setProtocol(MiraiProtocol.ANDROID_WATCH);
            noNetworkLog();

        }});
        bot.login();
        GlobalEventChannel.INSTANCE.registerListenerHost(groupMassage);
    }
}
