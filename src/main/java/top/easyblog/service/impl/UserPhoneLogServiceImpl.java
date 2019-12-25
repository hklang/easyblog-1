package top.easyblog.service.impl;

import top.easyblog.bean.UserPhoneLog;
import top.easyblog.mapper.UserPhoneLogMapper;
import top.easyblog.service.IUserPhoneLogService;
import org.springframework.stereotype.Service;

@Service
public class UserPhoneLogServiceImpl implements IUserPhoneLogService {

    final
    UserPhoneLogMapper userPhoneLogMapper;

    public UserPhoneLogServiceImpl(UserPhoneLogMapper userPhoneLogMapper) {
        this.userPhoneLogMapper = userPhoneLogMapper;
    }

    @Override
    public void saveSendCaptchaCode2User(String phone, String content) {
        userPhoneLogMapper.save(new UserPhoneLog(phone,content));
    }
}