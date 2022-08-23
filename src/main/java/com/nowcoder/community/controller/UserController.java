package com.nowcoder.community.controller;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    // 注入进来，上传路径、域名、地址、userService，以及获取当前登录用户  hostHolder
    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}${server.port}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contexPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;


    /**
     * 跳转到用户信息配置页面；需要登录，才能执行方法，自定义注解 @LoginRequired
     *
     * @return 用户配置页面
     */
    @LoginRequired
    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String getSettingPage() {
        return "/site/setting";
    }

    /**
     * 上传头像
     *
     * @param headerImage 获取上传头像图片的变量
     * @param model       携带错误信息给 template
     * @return 成功后返回主界面
     */
    @LoginRequired
    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model) {
        // 1. 检查上传文件
        if (headerImage == null) {
            model.addAttribute("headerErrorMsg", "在那之前，请先选择图片！");
            return "/site/setting";
        }

        // 2. 获取上传文件的名称，格式，以作重新生成文件名备用；
        String filename = headerImage.getOriginalFilename();
        // assert filename != null;
        String suffix = filename != null ? filename.substring(filename.lastIndexOf('.')) : "";
        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("headerErrorMsg", "文件格式不正确");
            return "/site/setting";
        }

        // 3. 生成随机的文件名称
        filename = CommunityUtil.generateUUID() + suffix;
        File dest = new File(uploadPath + "/pic/");
        if (!dest.exists() && !dest.isDirectory() && dest.mkdirs()) {
            logger.info("创建文件夹成功：" + dest.getAbsolutePath());
        }

        // 4. 文件实际存放路径 E:/work/Java/community/resource/uploadFile/pic
        dest = new File(dest.getAbsolutePath() + "/" + filename);

        try {
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败！" + e.getMessage());
            throw new RuntimeException("上传文件失败，服务器发生异常", e);
        }

        // 5. 更新用户头像数据
        User user = hostHolder.getUser();
        // E:/work/Java/community/bak/resource/upload/pic ==>
        // 这里是图片的访问路径，上面的是图片的存储路径
        String newHeaderUrl = domain + contexPath + "/user/header/" + filename;
        userService.updateHeader(user.getId(), newHeaderUrl);
        return "redirect:/index";
    }

    /**
     * <img th:src="${loginUser.headerUrl}" class="rounded-circle" style="width:30px;"> 这里请求
     * 路径不是瞎填的，E:/work/Java/community/bak/resource/upload/pic 所示是
     *
     * @param fileName
     * @param response
     */
    @RequestMapping(path = "/header/{fileName}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        // 1、需要找到服务器上存放文件的地址，本地路径
        fileName = uploadPath + "/pic/" + fileName;
        // 2、响应 网页(图片、文件)数据，首先获取文件格式，设置 response 相应数据的格式
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        response.setContentType("image/" + suffix);
        // 3、字节流 buffer 传输图片数据
        try (
                // java 新的语法在这里定义的流，结束的时候会帮助关闭，或者手动在 finally  结束
                OutputStream os = response.getOutputStream();   // 得到输出流
                FileInputStream fis = new FileInputStream(fileName);    // 获取文件输入字节流
        ) {
            // 输出的时候，不能一个一字节的输出，1kB 的输出
            byte[] buffer = new byte[1024];
            // 游标，
            int b = 0;
            while ((b = fis.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }
        } catch (FileNotFoundException e) {
            logger.error("打开头像文件失败" + e.getMessage());
        } catch (IOException e) {
            logger.error("读取头像失败" + e.getMessage());
        }
    }

    @LoginRequired
    @RequestMapping(path = "/password", method = RequestMethod.POST)
    public String updatePassword(String oldPassword, String newPassword, Model model) {
        // 1. 验证 新旧 密码不能相同
        if (StringUtils.isBlank(newPassword) || newPassword.equals(oldPassword)) {
            model.addAttribute("newPasswordMsg", "新密码错误，不为空且不能和原密码一致");
            return "/site/setting";
        }
        // 2. 验证旧密码是否正确
        User user = hostHolder.getUser();
        oldPassword = CommunityUtil.md5(oldPassword + user.getSalt());
        if (StringUtils.isBlank(oldPassword) || !user.getPassword().equals(oldPassword)) {
            model.addAttribute("oldPasswordMsg", "密码验证错误，请重新输入！");
            return "site/setting";
        }
        // 3. 修改密码跳转操作成功页面
        userService.updatePassword(user.getId(), newPassword, user.getSalt());
        model.addAttribute("msg", "密码修改成功！");
        model.addAttribute("target", "/index");
        return "/site/operate-result";
    }

}
