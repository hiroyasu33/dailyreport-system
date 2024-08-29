package com.techacademy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.constants.ErrorMessage;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.service.ReportService;
import com.techacademy.service.UserDetail;

@Controller
@RequestMapping("reports")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) { // @Autowired アノテーションを使って、ReportController クラスのコンストラクタに
                                                           // ReportService のインスタンスを注入しています。
        this.reportService = reportService;
    }

    // 日報一覧画面
    @GetMapping
    public String list(Model model) {
        // reportService.findAll()メソッドを呼び出して、すべてのレポートを取得します。そのリストのサイズを"listSize"という名前でモデルに追加します。
        model.addAttribute("listSize", reportService.findAll().size());

        // 再度reportService.findAll()メソッドを呼び出して、すべてのレポートを取得します。そのリストを"reportsList"という名前でモデルに追加します。
        model.addAttribute("reportsList", reportService.findAll());

        // 日報一覧画面に遷移
        return "reports/list"; // "reports/list"という名前のビュー（テンプレート）を返します。ビューは、listSizeとreportsListのデータを使用して、ユーザーにレポートの数と詳細を表示します。
    }

    // 日報詳細画面
    @GetMapping(value = "/{id}/")
    public String detail(@PathVariable Integer id, Model model) {
        // reportService.findById(id)メソッドを呼び出して、指定されたidのレポートを取得し、それを"report"という名前でモデルに追加します。
        model.addAttribute("report", reportService.findById(id));

        // 日報詳細画面に遷移
        return "reports/detail"; // reports/detail"という名前のビュー（テンプレート）を返します。このビューは詳細ページを表示します。
    }

    // 日報更新画面を表示
    @GetMapping("/{id}/update/")
    public String edit(@PathVariable("id") Integer id, Model model, Report report) {
        // reportService.findById(id)メソッドを呼び出して、指定されたidのレポートを取得し、それを"report"という名前でモデルに追加します。
        model.addAttribute("report", reportService.findById(id));

        // 日報更新画面に遷移
        return "reports/update";
    }

    // 日報更新処理
    @PostMapping("/{id}/update/")
    public String update(@PathVariable("id") Integer id, @Validated Report report, BindingResult res, Model model,
            @AuthenticationPrincipal UserDetail userDetail) {
        // @AuthenticationPrincipal UserDetail userDetail:認証されたユーザーの詳細情報を取得します。
        // 入力チェック バリデーションエラーがあるか確認します。
        if (res.hasErrors()) {
            // エラーがある場合は、再度編集画面に戻ります。
            return edit(id, model, report);
        }
        //　エラーを表示して編集画面に戻ります
        ErrorKinds result = reportService.update(report, id, userDetail);
        if (ErrorMessage.contains(result)) {
            model.addAttribute(ErrorMessage.getErrorName(result),ErrorMessage.getErrorValue(result));
            return edit(id, model, report);
        }
        // 一覧画面にリダイレクト
        return "redirect:/reports";

    }

    // 日報新規登録画面
    @GetMapping(value = "/add")
    // 新しいReportオブジェクトをモデルに追加します。 Model model ビューにデータを渡すためのオブジェクトです。
    public String create(@ModelAttribute Report report, Model model, @AuthenticationPrincipal UserDetail userDetail) {
        model.addAttribute("loginUser", userDetail.getEmployee());
        // 日報新規登録画面に遷移
        return "reports/new";
    }

    // 日報新規登録処理
    @PostMapping(value = "/add")
    public String add(@Validated Report report, BindingResult res, Model model,
            @AuthenticationPrincipal UserDetail userDetail) {

        // 入力チェック
        if (res.hasErrors()) {
            // エラーがある場合は、再度編集画面に戻ります。
            return create(report, model, userDetail);
        }



        Employee loginUser = userDetail.getEmployee(); // メソッドを呼び出して、ログインユーザーの情報を取得し、それをレポートに設定します。
        report.setEmployee(loginUser);
        ErrorKinds result = reportService.save(report, userDetail); // メソッドを呼び出して、レポートを保存します。エラー時にはエラーを表示
        if (ErrorMessage.contains(result)) {
            model.addAttribute(ErrorMessage.getErrorName(result),ErrorMessage.getErrorValue(result));
            return create(report, model, userDetail);
        }
        // 一覧画面にリダイレクト
        return "redirect:/reports";

    }

    // 従業員削除処理
    @PostMapping(value = "/{id}/delete")
    public String delete(@PathVariable Integer id, @AuthenticationPrincipal UserDetail userDetail, Model model) {

        ErrorKinds result = reportService.delete(id); // 指定されたidのレポートを削除します。削除の結果はErrorKinds型のresultに格納されます。
        // 削除結果にエラーが含まれているか確認します。
        if (ErrorMessage.contains(result)) {
            // エラーメッセージを取得し、モデルに追加します。
            model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
            model.addAttribute("report", reportService.findById(id));
            // 削除対象のレポートを再度取得し、モデルに追加します。
            return detail(id, model);
        }
        // 削除が成功した場合、レポートの一覧画面にリダイレクトします。
        return "redirect:/reports";
    }
}
