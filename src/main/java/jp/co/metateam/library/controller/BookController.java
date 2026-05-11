package jp.co.metateam.library.controller;
 
import java.util.List;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
 
import jakarta.validation.Valid;
import jp.co.metateam.library.model.BookMst;
import jp.co.metateam.library.model.BookMstDto;
import jp.co.metateam.library.service.BookMstService;
import lombok.extern.log4j.Log4j2;
 

/**
 * 書籍関連クラス
 */
@Log4j2 //ログ出力用
@Controller //このクラスは画面制御担当だとSpringに伝える
public class BookController { 

    private final BookMstService bookMstService; //Ssrviceを使うための変数

    @Autowired //serviceを自動で渡す
    public BookController(BookMstService bookMstService){
        this.bookMstService = bookMstService; 
    }


     //書籍一覧
    @GetMapping("/book/index") //画面表示
    public String index(Model model) {//HTMLにデータを渡すための箱
        List<BookMstDto> bookMstList = this.bookMstService.findAvailableWithStockCount();
        model.addAttribute("bookMstList", bookMstList);
        return "book/index";
    } //(book/index にアクセスされたら書籍一覧を取得して画面に渡してbook/index.html を表示する)


     //書籍登録画面
    @GetMapping("/book/add")
    public String add(Model model) {
        if (!model.containsAttribute("bookMstDto")) {
            model.addAttribute("bookMstDto", new BookMstDto());
        }
        return "book/add";
    } 
    
    @PostMapping("/book/add") //入力されたデータをサーバーに送信
    public String addBook(
        @Valid @ModelAttribute BookMstDto bookMstDto,
        BindingResult result, //エラー結果見る
        RedirectAttributes ra) { //エラー分を画面に返す

    try {
        //バリデーションチェック
        if (result.hasErrors()) {
            
            ra.addFlashAttribute("errTitle", //画面に書籍のエラーを表示
            result.getFieldError("title") != null ?
            result.getFieldError("title").getDefaultMessage() : null);

            ra.addFlashAttribute("errISBN", //画面にISBNのエラーを表示
            result.getFieldError("isbn") != null ?
            result.getFieldError("isbn").getDefaultMessage() : null);

            ra.addFlashAttribute("bookMstDto", bookMstDto); //入力した内容を保持
            ra.addFlashAttribute(
                    "org.springframework.validation.BindingResult.bookMstDto",
                    result);

            return "redirect:/book/add"; //入力画面に返す
        }

        // ISBN重複チェック
        BookMst existBook = this.bookMstService.findByIsbn(bookMstDto.getIsbn()); //DBに同じISBNがあるか検索

        if (existBook != null) { //同じISBNが見つかったら

            result.rejectValue(
                    "isbn",
                    "error.value",
                    "登録済みのISBNです"); //エラーを追加

            ra.addFlashAttribute("errISBN", "登録済みのISBNです"); //HTMLにエラーメッセージを渡してる
            ra.addFlashAttribute("bookMstDto", bookMstDto); //入力内容を保持
            ra.addFlashAttribute(
                    "org.springframework.validation.BindingResult.bookMstDto",
                    result);

            return "redirect:/book/add"; //入力画面に戻す
        }

        this.bookMstService.save(bookMstDto); //エラーがなかったのでDBに保存

        // 一覧画面へ
        return "redirect:/book/index"; //保存が成功したので一覧画面に移動
    } catch (Exception e) { //tryの中でエラーが起きた時の処理
        log.error(e.getMessage(), e); //エラー内容をログへ出力

        ra.addFlashAttribute("bookMstDto", bookMstDto); //入力した値を消さない

        return "redirect:/book/add"; //エラー時は書籍登録内容に戻す
    }
}

            
}


