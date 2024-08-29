package com.techacademy.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.repository.ReportRepository;

import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportService {

    private final ReportRepository reportRepository;

    @Autowired
    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    // 日報保存
    @Transactional
    public ErrorKinds save(Report report, @AuthenticationPrincipal UserDetail userDetail) {

        // ログイン中の従業員かつ入力した日付の日報データが存在する場合エラー
        if (reportRepository.findByReportDateAndEmployee(report.getReportDate(), userDetail.getEmployee()) != null) {
            return ErrorKinds.DATECHECK_ERROR; // エラーメッセージを表示して再度入力画面に戻る
        }

        report.setDeleteFlg(false); // 削除フラグをfalseに設定します。
        LocalDateTime now = LocalDateTime.now(); // 現在の日時を取得
        report.setCreatedAt(now); // 作成日時を設定します。
        report.setUpdatedAt(now); // 更新日時を設定します。

        reportRepository.save(report); // レポートをデータベースに保存します。
        return ErrorKinds.SUCCESS;
    }

    // 日報更新
    @Transactional
    // レポートを更新し、その結果をErrorKinds型で返します。
    public ErrorKinds update(Report report, Integer id, UserDetail userDetail) {

        // 指定されたidのレポートをデータベースから取得します。
        Report oldReport = findById(id);
        // 同じ日付とユーザーのレポートが既に存在するかを確認します。
        List<Report> existingReport = reportRepository.findByReportDateAndEmployee(report.getReportDate(),
                userDetail.getEmployee());
        // もし存在する且つ閲覧中のデータ以外の場合、エラーメッセージを返します。
        if (existingReport != null && !existingReport.isEmpty()) {
            Report firstReport = existingReport.get(0);
            if (firstReport.getId() != id) {
                return ErrorKinds.DATECHECK_ERROR;
            }
        }
        // oldReportの各フィールドを新しい値で更新します（setReportDate, setTitle, setContent）。
        oldReport.setReportDate(report.getReportDate());
        oldReport.setTitle(report.getTitle());
        oldReport.setContent(report.getContent());
        // 現在の日時を取得します。
        LocalDateTime now = LocalDateTime.now();
        // setUpdatedAt(now)で更新日時を設定します。
        oldReport.setUpdatedAt(now);
        // 更新されたレポートをデータベースに保存します。
        reportRepository.save(oldReport);
        return ErrorKinds.SUCCESS;
    }

    // 日報削除
    @Transactional
    // レポートを削除し、その結果をErrorKinds型で返します。
    public ErrorKinds delete(Integer id) {

        // 指定されたidのレポートをデータベースから取得します
        Report report = findById(id);
        // 現在の日時を取得します。
        LocalDateTime now = LocalDateTime.now();
        // 更新日時を設定します。
        report.setUpdatedAt(now);
        // 削除フラグをtrueに設定します。
        report.setDeleteFlg(true);

        return ErrorKinds.SUCCESS;
    }

    // 日報一覧表示処理
    // すべてのレポートを取得してリストとして返します。
    public List<Report> findAll() {
        // データベースからすべてのレポートを取得します。このメソッドはIterable<Report>を返します。
        Iterable<Report> iterable = reportRepository.findAll();
        // 空のリストを作成します。
        List<Report> list = new ArrayList<>();
        // iterableの各要素をリストに追加します。forEachメソッドを使って、list::addというメソッド参照を渡しています。
        iterable.forEach(list::add);
        // すべてのレポートを含むリストを返します。
        return list;
    }

    // 1件を検索
    public Report findById(Integer id) {
        // findByIdで検索
        Optional<Report> option = reportRepository.findById(id);
        // 取得できなかった場合はnullを返す
        Report report = option.orElse(null);
        return report;
    }

    // 指定された従業員に関連するレポートのリストを返します。
    public List<Report> findByEmployee(Employee employee) {
        // 指定された従業員に関連するレポートをデータベースから取得し、そのリストを返します。
        return reportRepository.findByEmployee(employee);
    }

}
