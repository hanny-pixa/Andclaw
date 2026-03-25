export default function Footer() {
  return (
    <footer className="relative border-t border-dark-border/50 py-12 px-6">
      <div className="max-w-4xl mx-auto">
        {/* top row */}
        <div className="flex flex-col md:flex-row items-center justify-between gap-6 mb-8">
          <div className="flex items-center gap-3">
            <img src="/icon.png" alt="Andclaw" className="w-8 h-8" />
            <span className="font-[family-name:var(--font-family-display)] text-lg font-bold">
              <span className="text-white">And</span>
              <span className="text-neon-cyan">claw</span>
            </span>
          </div>

          <div className="flex items-center gap-6">
            <a
              href="https://github.com/andforce/Andclaw"
              target="_blank"
              rel="noopener noreferrer"
              className="text-gray-400 hover:text-neon-cyan transition-colors text-sm"
            >
              GitHub
            </a>
            <a
              href="https://www.bilibili.com/video/BV1k8w4zeEL7"
              target="_blank"
              rel="noopener noreferrer"
              className="text-gray-400 hover:text-neon-cyan transition-colors text-sm"
            >
              演示视频
            </a>
            <a
              href="https://github.com/andforce/Andclaw/blob/main/LICENSE"
              target="_blank"
              rel="noopener noreferrer"
              className="text-gray-400 hover:text-neon-cyan transition-colors text-sm"
            >
              MIT 许可证
            </a>
          </div>
        </div>

        {/* disclaimer */}
        <p className="text-xs text-gray-600 text-center leading-relaxed mb-6">
          本项目仅供学习和研究使用。开发者不对因使用本软件导致的任何数据丢失、设备损坏或其他损失承担责任。
          屏幕 UI 数据和截图会发送给 LLM 提供商，请注意隐私保护。
        </p>

        {/* WeChat 群聊 */}
        <div className="flex flex-col items-center gap-2 mb-6">
          <img
            src="/wechat-group-qr.jpg"
            alt="扫码加入 Andclaw 微信群聊"
            className="w-[160px] h-auto rounded-lg"
          />
          <span className="text-xs text-gray-500">群聊: Andclaw</span>
        </div>

        <div className="text-center text-xs text-gray-700">
          Made with <span className="text-neon-purple">&#9829;</span> by Andclaw Team
        </div>
      </div>
    </footer>
  )
}
