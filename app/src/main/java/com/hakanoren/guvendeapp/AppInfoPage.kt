package com.hakanoren.guvendeapp

import FAQAdapter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import com.hakanoren.guvendeapp.databinding.AppInfoPageBinding

class AppInfoPage: AppCompatActivity() {

    private lateinit var binding: AppInfoPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = AppInfoPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val faqList = listOf(
            FAQItem("1. Yardım Eli Uygulaması Nedir?", "Yardım Eli Uygulaması, acil durumlarda yardıma ihtiyaç duyan insanlar için tek ekranda birden fazla işlemi aynı anda yapabilmenize olanak sağlıyan bir acil durum uygulamasıdır."),
            FAQItem("2. Uygulama veri topluyor mu?", "Yardım Eli Uygulaması, E-mail adresiniz hariç hiçbir veriyi saklamaz. Uygulama içinde sunduğunuz veriler tamamen kendi cihazlarınızda saklanır ve sunucularımıza iletilmez. Yardım Eli, hiçbir verinizi üçüncü taraf kişiler veya kurumlarla paylaşmaz."),
            FAQItem("3. Verilerim güvende mi?", "Evet, Yardım Eli uygulaması verilerinizi saklamaz ve kullanmaz."),
            FAQItem("4. Uygulama virüs içeriyor mu?", "Hayır, uygulama virüs içermez."),
            FAQItem("5. Hesabımı nasıl sileceğim?", "Hesabınızı silmek için, 'Ayarlar' sekmesinden Destek butonuyla bizimle iletişime geçmeniz ve hesabınızı silmek istediğinizi bildirmeniz yeterli. 24 saat içerisinde hesabınız silinecektir."),
            FAQItem("6. Uygulama ücretli mi?", "Hayır, uygulamamız tamamen ücretsiz ve açık kaynak kodludur. Bu sayede virüs içermediğini kodlara bakıp anlayabilir ve bir geliştiriciyseniz uygulamayı siz de geliştirebilirsiniz."),
            FAQItem("7. Uygulama çalışmıyor, ne yapmalıyım?", "Uygulama çalışmıyorsa, ayarlar bölümünden bizimle anında iletişim kurabilirsiniz. En kısa süre içerisinde sorununuzu çözmek için size yardımcı olacağız."),
            FAQItem("8. Uygulama internet'e ihtiyaç duyar mı?", "Uygulama internetsiz de çalışabilir fakat konum bilgisi gibi işlemleriniz için internet'e ihtiyaç duyar."),
        )
        val adapter = FAQAdapter(faqList)
        binding.faqRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.faqRecyclerView.adapter = adapter

    }

}