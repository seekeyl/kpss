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
        initialzeDao.init();
    }

    public void init() {
        initialzeDao.init();

        List<Group> list = groupMapper.selectList(null);
        if (list.isEmpty()) {
            Group group = new Group();
            group.setId(1);
            group.setName("默认分组");
            groupMapper.insert(group);
        }
        // keyMapper.selectList(null).forEach(System.out::println);
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

    public List<Group> getGroups() {
        return groupMapper.selectList(null);
    }

    public List<Key> getKeys(Integer groupId, Integer pageNo, Integer size) {
        Page<Key> page = new Page<>(pageNo, size);

        QueryWrapper<Key> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("group_id", groupId);
        Page<Key> keyPage = keyMapper.selectPage(page, queryWrapper);
        return keyPage.getRecords();
    }

    public void modifyKey(Key key) {
        Integer id = key.getId();
        if (id == null) {
            keyMapper.insert(key);
        } else {
            keyMapper.updateById(key);
        }
    }

    public void deleteKeyById(Integer id) {
        keyMapper.deleteById(id);
    }

    public void modifyGroup(Group group) {
        Integer id = group.getId();
        if (id == null) {
            groupMapper.insert(group);
        } else {
            groupMapper.updateById(group);
        }
    }

    public void deleteGroupById(Integer id) {
        groupMapper.deleteById(id);
    }

    public Secret getSecret() {
        return secretMapper.selectById(1);
    }

    public void updatePassword(String password) {
        Secret secret = new Secret();
        secret.setId(1);
        secret.setPassword(DigestUtil.md5Hex(password));
        secretMapper.updateById(secret);

    }
}
