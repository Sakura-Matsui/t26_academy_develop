package jp.co.metateam.library.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import io.micrometer.common.util.StringUtils;
import jp.co.metateam.library.model.BookMst;
import jp.co.metateam.library.model.BookMstDto;
import jp.co.metateam.library.repository.BookMstRepository;

@Service
public class BookMstService {

    private final BookMstRepository bookMstRepository;

    @Autowired
    public BookMstService(BookMstRepository bookMstRepository) {
        this.bookMstRepository = bookMstRepository;
    }

    public List<BookMstDto> findAvailableWithStockCount() {
        List<BookMst> books = this.bookMstRepository.findLimitedBook();
        List<BookMstDto> bookMstDtoList = new ArrayList<>();

        // 書籍の在庫数を取得
        // FIXME: 現状は書籍ID毎にDBに問い合わせている。一度のSQLで完了させたい。
        for (int i = 0; i < books.size(); i++) {
            BookMst book = books.get(i);
            BookMstDto dto = new BookMstDto();
            dto.setId(book.getId());
            dto.setIsbn(book.getIsbn());
            dto.setTitle(book.getTitle());
            bookMstDtoList.add(dto);
        }

        return bookMstDtoList;
    }

    // ISBN検索（後で重複チェックに使う）
    public BookMst findByIsbn(String isbn) {
        return this.bookMstRepository.findByIsbn(isbn).orElse(null);
    }

    // 保存処理
    @Transactional
    public void save(BookMstDto bookMstDto) {
        BookMst book = new BookMst();
        book.setIsbn(bookMstDto.getIsbn());
        book.setTitle(bookMstDto.getTitle());
        this.bookMstRepository.save(book);
    }
}