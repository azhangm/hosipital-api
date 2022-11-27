package com.example.hospital.api.controller.form;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 搜索内容形式
 *
 * @author zm
 * @date 2022/11/27
 */
@Data
public class SearchContentForm {

        @NotNull (message = "id不能为空")
        @Min(value = 1,message = "id不能小于1")
        private Integer id;
}
