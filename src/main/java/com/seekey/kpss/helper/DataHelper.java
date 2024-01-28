package com.seekey.kpss.helper;

import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seekey.kpss.commons.Sm2Util;
import com.seekey.kpss.dao.InitialzeDao;
import com.seekey.kpss.entity.*;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.util.Date;
import java.util.List;

@Service
public class DataHelper {

    InitialzeDao initialzeDao;

    GroupMapper groupMapper;

    KeyMapper keyMapper;

    SecretMapper secretMapper;

    @Autowired
    DataHelper(InitialzeDao initialzeDao, GroupMapper groupMapper, KeyMapper keyMapper, SecretMapper secretMapper) {
        this.initialzeDao = initialzeDao;
        this.groupMapper = groupMapper;
        this.keyMapper = keyMapper;
        this.secretMapper = secretMapper;
    }

    /**
     * 初始化数据
     */
    public void init() {
        initialzeDao.init();

        List<Group> list = groupMapper.selectList(null);
        if (list.isEmpty()) {
            Group group = new Group();
            group.setId(1);
            group.setName("默认分组");
            groupMapper.insert(group);
        }
        Secret secret = secretMapper.selectById(1);
        if (secret == null) {
            secret = new Secret();
            KeyPair keyPair = Sm2Util.generateSm2KeyPair();
            secret.setPrivateKey(Base64.encodeBase64String(keyPair.getPrivate().getEncoded()));
            secret.setPublicKey(Base64.encodeBase64String(keyPair.getPublic().getEncoded()));
            secret.setPassword(DigestUtil.md5Hex(""));
            secretMapper.insert(secret);
        }
    }

    /**
     * 获取所有分组
     * @return 分组列表
     */
    public List<Group> getGroups() {
        return groupMapper.selectList(null);
    }

    /**
     * 获取分组下的所有密钥
     * @param groupId 分组ID
     * @param pageNo 页码
     * @param size 每页大小
     * @return 密钥列表
     */
    public List<Key> getKeys(Integer groupId, Integer pageNo, Integer size) {
        Page<Key> page = new Page<>(pageNo, size);

        QueryWrapper<Key> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("group_id", groupId);
        Page<Key> keyPage = keyMapper.selectPage(page, queryWrapper);
        return keyPage.getRecords();
    }

    /**
     * 更新密钥信息
     * @param key 密钥
     */
    public void modifyKey(Key key) {
        Integer id = key.getId();
        if (id == null) {
            keyMapper.insert(key);
        } else {
            key.setUpdatedTime(new Date());
            keyMapper.updateById(key);
        }
    }

    /**
     * 删除密钥
     * @param id 密钥ID
     */
    public void deleteKeyById(Integer id) {
        keyMapper.deleteById(id);
    }

    /**
     * 更新分组信息
     * @param group 分组
     */
    public void modifyGroup(Group group) {
        Integer id = group.getId();
        if (id == null) {
            groupMapper.insert(group);
        } else {
            group.setUpdatedTime(new Date());
            groupMapper.updateById(group);
        }
    }

    /**
     * 删除分组
     * @param id 分组ID
     */
    public void deleteGroupById(Integer id) {
        groupMapper.deleteById(id);
    }

    /**
     * 获取加密信息
     * @return 密钥
     */
    public Secret getSecret() {
        return secretMapper.selectById(1);
    }

    /**
     * 更新读取密码
     * @param password 密码
     */
    public void updatePassword(String password) {
        Secret secret = new Secret();
        secret.setId(1);
        secret.setUpdatedTime(new Date());
        secret.setPassword(DigestUtil.md5Hex(password));
        secretMapper.updateById(secret);

    }
}
