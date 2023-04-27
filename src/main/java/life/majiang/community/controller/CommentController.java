package life.majiang.community.controller;

import life.majiang.community.cache.QuestionRateLimiter;
import life.majiang.community.dto.CommentCreateDTO;
import life.majiang.community.dto.CommentDTO;
import life.majiang.community.dto.ResultDTO;
import life.majiang.community.enums.CommentTypeEnum;
import life.majiang.community.exception.CustomizeErrorCode;
import life.majiang.community.model.Comment;
import life.majiang.community.model.User;
import life.majiang.community.service.CommentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by codedrinker on 2019/5/30.
 */
@Controller
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private QuestionRateLimiter questionRateLimiter;

    /**
     * 添加评论
     * @param commentCreateDTO 保存评论实体
     * @param request 请求
     * @return 结果集
     */
    @ResponseBody
    @RequestMapping(value = "/comment", method = RequestMethod.POST)
    public Object post(@RequestBody CommentCreateDTO commentCreateDTO,
                       HttpServletRequest request) {
        // 1、从session中拿到user信息
        User user = (User) request.getSession().getAttribute("user");
        // 1.1 判断用户是否登录
        if (user == null) {
            return ResultDTO.errorOf(CustomizeErrorCode.NO_LOGIN);
        }
        // 1.2 账号是否可用
        if (user.getDisable() != null && user.getDisable() == 1) {
            return ResultDTO.errorOf(CustomizeErrorCode.USER_DISABLE);
        }
        // 1.3 判断评论实体类是否为空或内容是否为空串
        if (commentCreateDTO == null || StringUtils.isBlank(commentCreateDTO.getContent())) {
            return ResultDTO.errorOf(CustomizeErrorCode.CONTENT_IS_EMPTY);
        }
        // 1.4 判断用户是否达到评论次数
        if (questionRateLimiter.reachLimit(user.getId())) {
            return ResultDTO.errorOf(CustomizeErrorCode.RATE_LIMIT);
        }
        // 2、添加一条新评论
        Comment comment = new Comment();
        comment.setParentId(commentCreateDTO.getParentId());
        comment.setContent(commentCreateDTO.getContent());
        comment.setType(commentCreateDTO.getType());
        comment.setGmtModified(System.currentTimeMillis());
        comment.setGmtCreate(System.currentTimeMillis());
        comment.setCommentator(user.getId());
        comment.setLikeCount(0L);
        commentService.insert(comment, user);
        return ResultDTO.okOf();
    }

    /**
     * 获取某篇博客的所有评论
     * @param id 博客id
     * @return 结果集
     */
    @ResponseBody
    @RequestMapping(value = "/comment/{id}", method = RequestMethod.GET)
    public ResultDTO<List<CommentDTO>> comments(@PathVariable(name = "id") Long id) {
        List<CommentDTO> commentDTOS = commentService.listByTargetId(id, CommentTypeEnum.COMMENT);
        return ResultDTO.okOf(commentDTOS);
    }
}
