package jp.co.metateam.library.model;

import java.security.Timestamp;

import jakarta.validation.constraints.NotEmpty; //バリデーション機能を使うためのimport
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 書籍マスタDTO
 */
@Getter //自動生成
@Setter //自動生成
public class BookMstDto {
    
    private Long id; //DBの番号

    //書籍名のルール
    @NotEmpty(message = "書籍名は必須です")
    @Size(max = 255, message = "書籍名は255文字以内で入力してください")
    private String title; 

    //ISBNのルール
    @NotEmpty(message = "ISBNは必須です")
    @Size(max = 13, message = "ISBNは13桁以内で入力してください")
    @Pattern(regexp = "^[0-9]+$", message = "ISBNは半角数字で入力してください")
    private String isbn; 
    
    private Timestamp deletedAt; //削除日時

}

