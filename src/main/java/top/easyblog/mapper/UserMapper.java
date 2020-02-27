package top.easyblog.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import top.easyblog.bean.User;
import top.easyblog.mapper.core.BaseMapper;

/**
 * @author huangxin
 */
@Repository
public interface UserMapper extends BaseMapper<User> {
    /**
     *
     * @param userPhone
     * @param password
     * @return
     */
    User getUserByUserPhoneAndPassword(@Param("userPhone") String userPhone, @Param("password") String password);

    /**
     *
     * @param userEmail
     * @param password
     * @return
     */
    User getUserByUserEmailAndPassword(@Param("userEmail") String userEmail, @Param("password") String password);

    /**
     *
     * @param nickname
     * @return
     */
    User getUserByNickname(@Param("nickname") String nickname);

    /**
     *
     * @param phone
     * @return
     */
    User getUserByPhone(@Param("phone") String phone);

    /**
     *
     *
     * @param email
     * @return
     */
    User getUserByEmail(@Param("email") String email);

    /**
     *
     * @param user
     * @return
     */
    int updateUserSelective(User user);

    /**
     *
     * @param user
     * @return
     */
    int updateByPrimaryKeySelective(@Param("user") User user);

}
